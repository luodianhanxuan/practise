package com.wangjg.pipeline;

/***
 * 对各个处理阶段的计算环境进行抽象，主要用于异常处理
 */
public interface PipeContext {

    void handleError(PipeException ex);

}
