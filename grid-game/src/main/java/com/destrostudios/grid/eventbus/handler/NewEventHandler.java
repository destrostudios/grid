package com.destrostudios.grid.eventbus.handler;

import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.NewEventbus;
import com.destrostudios.grid.eventbus.events.NewEvent;

import java.util.function.Supplier;

public interface NewEventHandler<E extends NewEvent> {
    void onEvent(E event, Supplier<EntityWorld> entityWorldSupplier);

    NewEventbus getEventBusInstance();

    Class<E> getEventClass();
}
