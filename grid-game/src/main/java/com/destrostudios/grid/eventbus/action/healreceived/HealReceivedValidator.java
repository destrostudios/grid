package com.destrostudios.grid.eventbus.action.healreceived;

import com.destrostudios.grid.components.properties.HealthPointsComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.EventValidator;

import java.util.function.Supplier;

public class HealReceivedValidator implements EventValidator<HealReceivedEvent> {
    @Override
    public boolean validate(HealReceivedEvent event, Supplier<EntityWorld> entityWorldSupplier) {
        return entityWorldSupplier.get().hasComponents(event.getTargetEntity(), HealthPointsComponent.class);
    }
}
