package com.wangjg.pipeline2.immutable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Pipeline<IN, OUT> {
    private final Collection<Pipe<?, ?>> pipes;

    private Pipeline(Pipe<IN, OUT> pipe) {
        this.pipes = Collections.singleton(pipe);
    }

    private Pipeline(Collection<Pipe<?, ?>> pipes) {
        this.pipes = pipes;
    }

    public static <IN, OUT> Pipeline<IN, OUT> of(Pipe<IN, OUT> pipe) {
        return new Pipeline<>(pipe);
    }

    public <NEW_OUT> Pipeline<IN, NEW_OUT> withNextPipe(Pipe<?, NEW_OUT> pipe) {
        final List<Pipe<?, ?>> newPipes = new ArrayList<>(this.pipes);
        newPipes.add(pipe);
        return new Pipeline<>(newPipes);
    }

    public OUT process(IN input) {
        Object output = input;
        for (final Pipe pipe : pipes) {
            output = pipe.process(output);
        }
        return (OUT) output;
    }
}
