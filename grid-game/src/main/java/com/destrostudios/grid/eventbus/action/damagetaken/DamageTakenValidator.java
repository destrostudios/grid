package com.destrostudios.grid.eventbus.action.damagetaken;

import com.destrostudios.grid.components.properties.HealthPointsComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.EventValidator;

import java.util.function.Supplier;

public class DamageTakenValidator implements EventValidator<DamageTakenEvent> {
    @Override
    public boolean validate(DamageTakenEvent event, Supplier<EntityWorld> entityWorldSupplier) {
        return entityWorldSupplier.get().hasComponents(event.getTargetEntity(), HealthPointsComponent.class);
    }
}
