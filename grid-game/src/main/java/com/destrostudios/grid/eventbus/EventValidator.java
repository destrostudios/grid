package com.destrostudios.grid.eventbus;

import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.Event;

import java.util.function.Supplier;

public interface EventValidator<E extends Event> {
    boolean validate(E event, Supplier<EntityWorld> entityWorldSupplier);
}
