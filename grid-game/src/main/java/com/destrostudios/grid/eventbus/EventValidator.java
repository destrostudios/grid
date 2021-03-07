package com.destrostudios.grid.eventbus;

import com.destrostudios.grid.entities.EntityData;
import java.util.function.Supplier;

public interface EventValidator<E extends Event> {
    boolean validate(E event, Supplier<EntityData> entityWorldSupplier);
}
