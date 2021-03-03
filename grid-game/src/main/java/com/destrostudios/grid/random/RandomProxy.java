package com.destrostudios.grid.random;

public interface RandomProxy {

    int nextInt(int upperExclusive);

    default int nextInt(int lowerInclusive, int upperExclusive) {
        return lowerInclusive + nextInt(upperExclusive - lowerInclusive);
    }
}
