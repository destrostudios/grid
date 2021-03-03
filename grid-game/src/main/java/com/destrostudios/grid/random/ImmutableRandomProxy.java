package com.destrostudios.grid.random;

import java.util.function.IntUnaryOperator;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ImmutableRandomProxy implements RandomProxy {

    private final IntUnaryOperator random;

    @Override
    public int nextInt(int upperExclusive) {
        return random.applyAsInt(upperExclusive);
    }
}
