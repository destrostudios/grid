package com.destrostudios.grid.eventbus.update.lifespan;

import com.destrostudios.grid.eventbus.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LifespanUpdateEvent implements Event {
  private final int playerEntity;
}
