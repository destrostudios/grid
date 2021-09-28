package com.destrostudios.grid.eventbus.update.maxhp;

import com.destrostudios.grid.eventbus.update.PropertiePointsChangedEvent;

public class MaxHealthPointsChangedEvent extends PropertiePointsChangedEvent {
  public MaxHealthPointsChangedEvent(int entity, int newPoints) {
    super(entity, newPoints);
  }
}
