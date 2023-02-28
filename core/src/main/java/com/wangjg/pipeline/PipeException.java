package com.wangjg.pipeline;

public class PipeException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * 抛出异常的 Pipe 实例
     */
    public final Pipe<?, ?> pipe;

    public final Object input;

    public PipeException(Pipe<?, ?> pipe, Object input, String message) {
        super(message);
        this.pipe = pipe;
        this.input = input;
    }

    public PipeException(Pipe<?, ?> pipe, Object input, String message, Throwable cause) {
        super(message, cause);
        this.pipe = pipe;
        this.input = input;
    }

}
