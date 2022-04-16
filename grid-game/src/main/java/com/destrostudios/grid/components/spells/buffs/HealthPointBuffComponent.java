package com.destrostudios.grid.components.spells.buffs;

import com.destrostudios.grid.components.Component;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class HealthPointBuffComponent implements Component, BuffComponent {
  int buffAmount;
  int buffDuration;
  BuffType buffType;
}
