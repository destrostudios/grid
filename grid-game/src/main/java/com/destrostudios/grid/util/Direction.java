package com.destrostudios.grid.util;

import lombok.Getter;

@Getter
public enum Direction {
  NONE(0, 0),
  UP(0, 1),
  UP_RIGHT(1, 1),
  RIGHT(1, 0),
  DOWN_RIGHT(1, -1),
  DOWN(0, -1),
  DOWN_LEFT(-1, -1),
  LEFT(-1, 0),
  UP_LEFT(-1, 1);

  private final int deltaX;
  private final int deltaY;

  Direction(int deltaX, int deltaY) {
    this.deltaX = deltaX;
    this.deltaY = deltaY;
  }
}
