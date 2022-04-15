package com.destrostudios.grid.eventbus;

import com.destrostudios.grid.entities.EntityData;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Supplier;

@AllArgsConstructor
@Getter
public class TriggeredEventHandler {
  private Event event;
  private EventHandler eventHandler;

  public void onEvent(Supplier<EntityData> entityDataSupplier) {
    eventHandler.onEvent(event, entityDataSupplier);
  }
}
