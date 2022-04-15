package com.destrostudios.grid.eventbus.update.position;

import com.destrostudios.grid.components.map.PositionComponent;
import com.destrostudios.grid.eventbus.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PositionUpdateEvent implements Event {
  private final int entity;
  private final PositionComponent newPosition;
}
