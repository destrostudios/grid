package com.destrostudios.grid.eventbus;

import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.Event;

import java.util.function.Supplier;

public interface EventHandler<E extends Event> {
    void onEvent(E event, Supplier<EntityWorld> entityWorldSupplier);
}
