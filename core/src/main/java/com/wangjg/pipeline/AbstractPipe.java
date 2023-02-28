package com.wangjg.pipeline;

import java.util.concurrent.TimeUnit;

public abstract class AbstractPipe<IN, OUT> implements Pipe<IN, OUT> {

    protected volatile Pipe<?, ?> next;

    protected volatile PipeContext context;

    @Override
    public void setNext(Pipe<?, ?> next) {
        this.next = next;
    }

    @Override
    public void process(IN input) throws InterruptedException {
        try {
            OUT out = doProcess(input);
            if (next != null) {
                //noinspection unchecked
                ((Pipe<OUT, ?>) next).process(out);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (PipeException e) {
            context.handleError(e);
        }
    }

    public abstract OUT doProcess(IN input) throws PipeException;

    @Override
    public void init(PipeContext context) {
        this.context = context;
    }

    @Override
    public void shutdown(long timeout, TimeUnit timeUnit) {

    }
}
