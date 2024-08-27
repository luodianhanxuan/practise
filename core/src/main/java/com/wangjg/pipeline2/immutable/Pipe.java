package com.wangjg.pipeline2.immutable;

public interface Pipe<IN, OUT> {
    OUT process(IN input);
}
