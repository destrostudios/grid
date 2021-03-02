package com.destrostudios.grid.eventbus.update.poison;

import com.destrostudios.grid.components.properties.PoisonsComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.EventValidator;

import java.util.function.Supplier;

public class UpdatePoisonValidator implements EventValidator<UpdatePoisonsEvent> {
    @Override
    public boolean validate(UpdatePoisonsEvent event, Supplier<EntityWorld> entityWorldSupplier) {
        return entityWorldSupplier.get().hasComponents(event.getEntity(), PoisonsComponent.class)
                && !entityWorldSupplier.get().getComponent(event.getEntity(), PoisonsComponent.class).getPoisonsEntities().isEmpty();
    }
}
