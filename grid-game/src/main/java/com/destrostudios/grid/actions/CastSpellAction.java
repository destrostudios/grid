package com.destrostudios.grid.actions;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class CastSpellAction implements Action {
  private int targetX;
  private int targetY;
  private String playerIdentifier;
  private int spell;
}
