package com.destrostudios.grid.eventbus.add.summon;

import com.destrostudios.grid.eventbus.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SummonBuffEvent implements Event {
  private final int summonEntity;
  private final int spell;
}
