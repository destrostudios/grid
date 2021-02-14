package com.destrostudios.grid.eventbus.handler;

import com.destrostudios.grid.components.properties.HealthPointsComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.events.HealthPointsChangedEvent;

import java.util.function.Supplier;

public class HealthPointsChangedHandler implements EventHandler<HealthPointsChangedEvent> {
    @Override
    public void onEvent(HealthPointsChangedEvent event, Supplier<EntityWorld> entityWorldSupplier) {
        entityWorldSupplier.get().remove(event.getEntity(), HealthPointsComponent.class);
        entityWorldSupplier.get().addComponent(event.getEntity(), new HealthPointsComponent(event.getNewHealthPoints()));
    }
}
