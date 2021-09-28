package com.destrostudios.grid.eventbus.action.die;

import com.destrostudios.grid.eventbus.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DieEvent implements Event {
  private final int entity;
}
