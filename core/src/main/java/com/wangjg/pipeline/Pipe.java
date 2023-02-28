package com.wangjg.pipeline;

import java.util.concurrent.TimeUnit;

/**
 * 对处理阶段的抽象
 *
 * @param <IN>  输入参数类型
 * @param <OUT> 输出参数类型
 */
public interface Pipe<IN, OUT> {

    /**
     * 设置当前 Pipe 实例的下个Pipe 实例
     */
    void setNext(Pipe<?, ?> next);

    /**
     * 对输入的元素进行处理，并将处理结果作为下一个 Pipe 实例的输入
     */
    void process(IN input) throws InterruptedException;

    /**
     * 初始化
     */
    void init(PipeContext context);

    /**
     * 关闭
     */
    void shutdown(long timeout, TimeUnit timeUnit);
}
