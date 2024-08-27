package com.wangjg.guavacase.interner;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import lombok.Getter;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

@Getter
public enum SyncKeyHelper {
    STRING;

    private Interner<Object> pool;

    SyncKeyHelper() {
        this.pool = Interners.newWeakInterner();
    }

    static class Counter {
        @Getter
        private int i;
        private final String businessId;

        public Counter(String businessId) {
            this.businessId = businessId;
        }

        public void incr() {
            synchronized (SyncKeyHelper.STRING.pool.intern(businessId)) {
                i++;
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        String info = "baidu";
        String strA = "www.baidu.com";//存储在运行时常量池
        String strB = "www." + info + ".com";
        System.out.println(strA == strB);//false
        System.out.println(SyncKeyHelper.STRING.pool.intern(strA) == SyncKeyHelper.STRING.pool.intern(strB));//true

        int threadNum = 100;
        int loop = 10000000;

        Counter[] counters = new Counter[]{
                new Counter("1"), new Counter("2"), new Counter("3"), new Counter("4"), new Counter("5"),
                new Counter("6"), new Counter("7"), new Counter("8"), new Counter("9"), new Counter("10")};

        CountDownLatch countDownLatch = new CountDownLatch(threadNum * loop);

        Random random = new Random();
        Runnable runnable = () -> {
            try {
                Counter counter = counters[random.nextInt(10)];
                for (int j = 0; j < loop; j++) {
                    counter.incr();
                    countDownLatch.countDown();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        for (int i = 0; i < threadNum; i++) {
            new Thread(runnable).start();
        }

        countDownLatch.await();
        for (Counter counter : counters) {
            System.out.printf("businessId: %s，i：%s %n", counter.businessId, counter.getI());
        }
    }
}

