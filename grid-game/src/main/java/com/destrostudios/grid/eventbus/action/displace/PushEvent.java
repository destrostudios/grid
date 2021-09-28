package com.destrostudios.grid.eventbus.action.displace;

import com.destrostudios.grid.eventbus.Event;
import com.destrostudios.grid.util.Direction;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PushEvent implements Event {
  private final int entityToDisplace;
  private final int strength;
  private final Direction direction;
}
