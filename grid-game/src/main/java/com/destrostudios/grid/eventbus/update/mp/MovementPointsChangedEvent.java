package com.destrostudios.grid.eventbus.update.mp;

import com.destrostudios.grid.eventbus.update.PropertiePointsChangedEvent;

public class MovementPointsChangedEvent extends PropertiePointsChangedEvent {
  public MovementPointsChangedEvent(int entity, int newPoints) {
    super(entity, newPoints);
  }
}
