package com.destrostudios.grid.eventbus.update.maxmp;

import com.destrostudios.grid.eventbus.update.PropertiePointsChangedEvent;

public class MaxMovementPointsChangedEvent extends PropertiePointsChangedEvent {
  public MaxMovementPointsChangedEvent(int entity, int newPoints) {
    super(entity, newPoints);
  }
}
