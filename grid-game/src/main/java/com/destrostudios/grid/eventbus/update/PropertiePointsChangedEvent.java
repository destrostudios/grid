package com.destrostudios.grid.eventbus.update;

import com.destrostudios.grid.eventbus.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public abstract class PropertiePointsChangedEvent implements Event {
  private int entity;
  private int newPoints;
}
