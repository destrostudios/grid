package com.destrostudios.grid.eventbus.action.walk;

import com.destrostudios.grid.components.map.PositionComponent;
import com.destrostudios.grid.eventbus.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WalkEvent implements Event {
  public final int entity;
  public final PositionComponent positionComponent;
}
