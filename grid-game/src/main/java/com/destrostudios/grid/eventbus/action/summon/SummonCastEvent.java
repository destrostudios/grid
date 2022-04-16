package com.destrostudios.grid.eventbus.action.summon;

import com.destrostudios.grid.components.map.PositionComponent;
import com.destrostudios.grid.eventbus.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SummonCastEvent implements Event {
  private final PositionComponent spawnPosition;
  private final int summonerEntity;
  private final String summonFile;
  private final String sourceId;
}
