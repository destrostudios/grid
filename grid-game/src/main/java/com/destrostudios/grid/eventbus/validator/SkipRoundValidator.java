package com.destrostudios.grid.eventbus.validator;

import com.destrostudios.grid.components.character.RoundComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.events.RoundSkippedEvent;

import java.util.Optional;
import java.util.function.Supplier;

public class SkipRoundValidator implements EventValidator<RoundSkippedEvent> {
    @Override
    public boolean validate(RoundSkippedEvent event, Supplier<EntityWorld> entityWorldSupplier) {
        EntityWorld entityWorld = entityWorldSupplier.get();
        Optional<RoundComponent> component = entityWorld.getComponent(event.getEntity(), RoundComponent.class);
        return component.isPresent();
    }
}
