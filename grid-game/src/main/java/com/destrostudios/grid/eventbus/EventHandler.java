package com.destrostudios.grid.eventbus;

import com.destrostudios.grid.entities.EntityData;

import java.util.function.Supplier;

public interface EventHandler<E extends Event> {
  void onEvent(E event, Supplier<EntityData> entityDataSupplier);
}
