package com.destrostudios.grid.components.spells.buffs;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
public class DamageBuffComponent extends BuffComponent {
  public DamageBuffComponent(int buffAmount, int buffDuration, BuffType buffType) {
    super(buffAmount, buffDuration, buffType);
  }
}
