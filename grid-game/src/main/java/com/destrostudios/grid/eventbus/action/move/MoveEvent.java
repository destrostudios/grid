package com.destrostudios.grid.eventbus.action.move;

import com.destrostudios.grid.components.map.PositionComponent;
import com.destrostudios.grid.eventbus.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MoveEvent implements Event {
  private final int entity;
  private final PositionComponent positionComponent;
  private final MoveType moveType;
}
