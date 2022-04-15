package com.destrostudios.grid.eventbus.action.swap;

import com.destrostudios.grid.eventbus.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SwapEvent implements Event {
  private int sourceEntity;
  private int targetEntity;
}
