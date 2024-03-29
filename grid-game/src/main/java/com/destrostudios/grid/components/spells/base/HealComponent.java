package com.destrostudios.grid.components.spells.base;

import com.destrostudios.grid.components.Component;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class HealComponent implements Component {
  int minHeal;
  int maxHeal;
  boolean targetingEnemies;
}
