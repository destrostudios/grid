package com.destrostudios.grid.eventbus.action.endturn;

import com.destrostudios.grid.eventbus.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class EndTurnEvent implements Event {
  private final int endTurnEntity;
}
