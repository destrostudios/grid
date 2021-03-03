package com.destrostudios.grid.eventbus.update.turn;

import com.destrostudios.grid.components.character.TurnComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.EventValidator;

import java.util.function.Supplier;

public class UpdatedTurnValidator implements EventValidator<UpdatedTurnEvent> {
    @Override
    public boolean validate(UpdatedTurnEvent event, Supplier<EntityWorld> entityWorldSupplier) {
        EntityWorld entityWorld = entityWorldSupplier.get();
        TurnComponent component = entityWorld.getComponent(event.getEntity(), TurnComponent.class);
        return component != null;
    }
}
