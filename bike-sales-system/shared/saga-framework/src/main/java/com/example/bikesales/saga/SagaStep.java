package com.example.bikesales.saga;

import java.util.concurrent.CompletableFuture;

public interface SagaStep<T> {
    CompletableFuture<SagaStepResult> execute(T context);
    CompletableFuture<Void> compensate(T context);
    String getStepName();
}
