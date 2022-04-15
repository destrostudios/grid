package com.destrostudios.grid.eventbus.update.turn;

import com.destrostudios.grid.eventbus.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdatedTurnEvent implements Event {
  private final int entity;
}
