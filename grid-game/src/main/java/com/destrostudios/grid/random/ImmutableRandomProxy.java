package com.destrostudios.grid.random;

import lombok.AllArgsConstructor;

import java.util.function.IntUnaryOperator;

@AllArgsConstructor
public class ImmutableRandomProxy implements RandomProxy {

  private final IntUnaryOperator random;

  @Override
  public int nextInt(int upperExclusive) {
    return random.applyAsInt(upperExclusive);
  }
}
