package com.github.zimablue.pufftower.api.dungeon.generator;

public interface GenerationContext {
    int generated();

    void setGenerated(int generated);

    default void incrementGenerated() {
        setGenerated(generated() + 1);
    }
}
