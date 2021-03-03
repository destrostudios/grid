package com.destrostudios.grid.random;

import java.util.function.IntUnaryOperator;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class MutableRandomProxy implements RandomProxy {

    @Setter
    private IntUnaryOperator random;

    @Override
    public int nextInt(int upperExclusive) {
        return random.applyAsInt(upperExclusive);
    }
}
