package com.destrostudios.grid.random;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.function.IntUnaryOperator;

@NoArgsConstructor
@AllArgsConstructor
public class MutableRandomProxy implements RandomProxy {

  @Setter private IntUnaryOperator random;

  @Override
  public int nextInt(int upperExclusive) {
    return random.applyAsInt(upperExclusive);
  }
}
