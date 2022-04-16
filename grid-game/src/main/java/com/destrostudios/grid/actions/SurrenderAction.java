package com.destrostudios.grid.actions;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class SurrenderAction implements Action {
  String playerIdentifier;
  int player;

  @Override
  public String getPlayerIdentifier() {
    return playerIdentifier;
  }
}
