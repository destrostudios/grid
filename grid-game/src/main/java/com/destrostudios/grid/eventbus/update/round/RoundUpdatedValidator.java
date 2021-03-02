package com.destrostudios.grid.eventbus.update.round;

import com.destrostudios.grid.components.character.TurnComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.EventValidator;

import java.util.function.Supplier;

public class RoundUpdatedValidator implements EventValidator<RoundUpdatedEvent> {
    @Override
    public boolean validate(RoundUpdatedEvent event, Supplier<EntityWorld> entityWorldSupplier) {
        EntityWorld entityWorld = entityWorldSupplier.get();
        TurnComponent component = entityWorld.getComponent(event.getEntity(), TurnComponent.class);
        return component != null;
    }
}
