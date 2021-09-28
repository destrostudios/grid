package com.destrostudios.grid.eventbus.action.healreceived;

import com.destrostudios.grid.eventbus.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class HealReceivedEvent implements Event {
  private final int heal;
  private final int targetEntity;
}
