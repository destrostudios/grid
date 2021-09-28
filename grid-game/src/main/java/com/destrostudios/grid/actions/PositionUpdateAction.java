package com.destrostudios.grid.actions;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class PositionUpdateAction implements Action {
  private int newX;
  private int newY;

  private String playerIdentifier;

  @Override
  public String getPlayerIdentifier() {
    return playerIdentifier;
  }
}
