package com.wangjg.pipeline;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AbstractPipeline<IN, OUT> extends AbstractPipe<IN, OUT> implements Pipeline<IN, OUT> {

    private final Queue<Pipe<?, ?>> pipes = new LinkedList<>();
    private final ExecutorService executorService;

    public AbstractPipeline() {
        // 创建固定线程数为 1 的线程池，整型最大数的 LinkedBlockingQueue 的缓存队列
        this(Executors.newSingleThreadExecutor(r -> new Thread(r, "SimplePipeLine-thread")));
    }

    public AbstractPipeline(final ExecutorService executorService) {
        super();
        this.executorService = executorService;
    }

    @Override
    public void setNext(Pipe<?, ?> next) {
        super.setNext(next);
    }

    @Override
    public void process(IN input) throws InterruptedException {
        @SuppressWarnings("unchecked")
        Pipe<IN, ?> firstPipe = (Pipe<IN, ?>) pipes.peek();
        if (firstPipe != null) {
            firstPipe.process(input);
        }
    }

    @Override
    public OUT doProcess(IN input) throws PipeException {

        return null;
    }


    @Override
    public void init(PipeContext context) {
        LinkedList<Pipe<?, ?>> pipesList = (LinkedList<Pipe<?, ?>>) pipes;
        Pipe<?, ?> prevPipe = this;

        // 设置处理任务的先后顺序
        for (Pipe<?, ?> pipe : pipesList) {
            prevPipe.setNext(pipe);
            prevPipe = pipe;
        }

        Runnable task = () -> {
            for (Pipe<?, ?> pipe : pipes) {
                pipe.init(context);
            }
        };

        executorService.submit(task);
    }

    @Override
    public void shutdown(long timeout, TimeUnit unit) {
        Pipe<?, ?> pipe;

        while ((pipe = pipes.poll()) != null) {
            pipe.shutdown(timeout, unit);
        }

        executorService.shutdown();
    }

    @Override
    public void addPipe(Pipe<?, ?> pipe) {
        pipes.add(pipe);
    }

    public PipeContext newDefaultPipeContext() {
        return ex ->
                executorService.submit(
                        () -> {
                            ex.printStackTrace();
                        });
    }
}
