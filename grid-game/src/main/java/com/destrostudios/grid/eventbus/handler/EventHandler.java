package com.destrostudios.grid.eventbus.handler;

import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.Eventbus;
import com.destrostudios.grid.eventbus.events.Event;

import java.util.function.Supplier;

public interface EventHandler<E extends Event> {
    void onEvent(E event, Supplier<EntityWorld> entityWorldSupplier);

    Class<E> getEventClass();
}
