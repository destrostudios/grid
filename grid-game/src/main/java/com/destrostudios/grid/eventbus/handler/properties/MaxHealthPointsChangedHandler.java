package com.destrostudios.grid.eventbus.handler.properties;

import com.destrostudios.grid.components.properties.MaxHealthComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.events.properties.MaxHealthPointsChangedEvent;
import com.destrostudios.grid.eventbus.handler.EventHandler;

import java.util.Optional;
import java.util.function.Supplier;

public class MaxHealthPointsChangedHandler implements EventHandler<MaxHealthPointsChangedEvent> {
    @Override
    public void onEvent(MaxHealthPointsChangedEvent event, Supplier<EntityWorld> entityWorldSupplier) {
        EntityWorld entityWorld = entityWorldSupplier.get();
        entityWorld.remove(event.getEntity(), MaxHealthComponent.class);
        entityWorld.addComponent(event.getEntity(), new MaxHealthComponent(event.getMaxHealtPoints()));
    }
}
