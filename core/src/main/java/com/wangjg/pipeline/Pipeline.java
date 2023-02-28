package com.wangjg.pipeline;

/**
 * 对复合 Pipe 的抽象。一个 Pipe Line 实例可包含多个 Pipe 实例
 */
public interface Pipeline<IN, OUT> extends Pipe<IN, OUT> {

    void addPipe(Pipe<?, ?> pipe);
}
