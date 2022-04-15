package com.destrostudios.grid.eventbus.action.beginturn;

import com.destrostudios.grid.eventbus.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BeginTurnEvent implements Event {
  private final int beginTurnEntity;
}
