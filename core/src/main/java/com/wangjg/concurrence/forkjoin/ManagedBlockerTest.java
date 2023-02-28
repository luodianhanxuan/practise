package com.wangjg.concurrence.forkjoin;

import org.junit.Test;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ManagedBlockerTest {

    static String threadDateTimeInfo() {
        return DateTimeFormatter.ISO_TIME.format(LocalTime.now()) + Thread.currentThread().getName();
    }

    static final int MAX_CAP = 0x7fff;        // max #workers - 1

    @Test
    public void test() {
        System.out.println(Math.min(MAX_CAP, Runtime.getRuntime().availableProcessors()));
    }

    /**
     * 对于IO阻塞型任务，当然可以设置较大的parallelism参数保证并发数防止任务IO阻塞耗时的时候没有空闲工作线程
     * 来执行新提交的IO阻塞任务导致CPU空闲，从而线程池的吞吐率不高，
     * 参见下方例子，例子中创建48个IO阻塞任务并同时提交到ForkJoinPool执行，
     * ForkJoinPool内部的静态ForkJoinPool实例作为全局线程池，默认parallelism参数为：Math.min(MAX_CAP, Runtime.getRuntime().availableProcessors())
     * 我的电脑返回值是 12
     * <p>
     * 由于每个任务固定阻塞2秒，而线程池的parallelism = 12，因此总的耗时时间为(96 / 12) * 2 = 16秒
     */
    @Test
    public void test1() {
        List<RecursiveTask<String>> tasks = Stream.generate(() -> new RecursiveTask<String>() {
            @Override
            protected String compute() {
                System.out.println(threadDateTimeInfo() + ": simulate io task blocking for 2 seconds...");
                try {
                    // 模拟 IO 调用阻塞
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return threadDateTimeInfo() + ": io blocking task returns successfully";
            }
        }).limit(96).collect(Collectors.toList());

        tasks.forEach(ForkJoinTask::fork);

        tasks.forEach(e -> {
            try {
                System.out.println(e.get());
            } catch (InterruptedException | ExecutionException ex) {
                ex.printStackTrace();
            }
        });

    }

    /**
     * 实际上我们希望任务阻塞时线程池能够提供额外的工作线程来执行新的IO任务而不是阻塞等待，
     * 最终实现 48 个线程全部提交并阻塞并在2秒后全部返回。这样吞吐量增加了10倍，
     * 那如何实现此种需求？new ForkJoinPool(100)创建100个工作线程显然可以满足要求，
     * 但是在这种情况下对于纯计算的任务由于线程切换也会导致cpu效率下降。
     * 更有效方法是对IO阻塞型任务提供一个ManagedBlocker让线程池知道当前任务即将阻塞，
     * 因此需要创建新的补偿工作线程来执行新的提交任务
     * <p>
     * 测试结果如下：结果符合预期要求，98个任务均在2秒时间内返回
     */
    @Test
    public void test2() {
        List<IOBlockerTask<String>> tasks = Stream.generate(() -> new IOBlockerTask<>(() -> {
            System.out.println(threadDateTimeInfo() + ": simulate io task blocking for 2 seconds...");
            try {
                // 模拟 IO 调用阻塞
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return threadDateTimeInfo() + ": io blocking task returns successfully";

        })).limit(96).collect(Collectors.toList());

        tasks.forEach(ForkJoinTask::fork);
        tasks.forEach(e -> {
            try {
                System.out.println(e.get());
            } catch (InterruptedException | ExecutionException ex) {
                ex.printStackTrace();
            }
        });
    }

    static class IOBlockerTask<T> extends RecursiveTask<T> {
        private final transient MyManagedBlockerImpl<T> blocker;

        public IOBlockerTask(Supplier<T> supplier) {
            this.blocker = new MyManagedBlockerImpl<>(supplier);
        }

        static class MyManagedBlockerImpl<T> implements ForkJoinPool.ManagedBlocker {
            private final Supplier<T> supplier;
            private T result;

            public MyManagedBlockerImpl(Supplier<T> supplier) {
                this.supplier = supplier;
            }

            @Override
            public boolean block() {
                result = supplier.get();
                return true;
            }

            @Override
            public boolean isReleasable() {
                return false;
            }
        }

        @Override
        protected T compute() {
            try {
                ForkJoinPool.managedBlock(blocker);
                setRawResult(blocker.result);
                return getRawResult();
            } catch (InterruptedException e) {
                throw new Error(e);
            }
        }
    }
}
