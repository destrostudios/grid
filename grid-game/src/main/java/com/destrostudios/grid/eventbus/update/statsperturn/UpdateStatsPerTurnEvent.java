package com.destrostudios.grid.eventbus.update.statsperturn;

import com.destrostudios.grid.eventbus.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UpdateStatsPerTurnEvent implements Event {
  private final int entity;
}
