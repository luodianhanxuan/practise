package com.wangjg.pipeline;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public abstract class AbstractParallelPipe<IN, OUT, V> extends AbstractPipe<IN, OUT> {

    private final ExecutorService executorService;
    private final BlockingQueue<IN> queue;

    public AbstractParallelPipe(BlockingQueue<IN> queue, ExecutorService executorService) {
        this.queue = queue;
        this.executorService = executorService;
    }

    /**
     * 用于根据指定的输入元素 input 构造一组子任务
     */
    protected abstract List<Callable<V>> buildTasks(IN input) throws Exception;

    /**
     * 对各个子任务的处理结果进行合并，形成相应输入元素的输出结果
     */
    protected abstract OUT combineResults(List<Future<V>> subTaskResults) throws Exception;

    @Override
    public OUT doProcess(IN input) throws PipeException {
        OUT out;
        try {
            out = combineResults(invokeParallel(buildTasks(input)));
        } catch (Exception e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new PipeException(this, input, "Task failed", e);
        }

        return out;
    }

    private List<Future<V>> invokeParallel(List<Callable<V>> tasks) throws InterruptedException {
        return executorService.invokeAll(tasks);
    }
}
