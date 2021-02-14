package com.destrostudios.grid.eventbus.handler;

import com.destrostudios.grid.components.properties.MaxHealthComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.events.MaxHealPointsChangedEvent;

import java.util.Optional;
import java.util.function.Supplier;

public class MaxHealtPointsChangedHandler implements EventHandler<MaxHealPointsChangedEvent> {
    @Override
    public void onEvent(MaxHealPointsChangedEvent event, Supplier<EntityWorld> entityWorldSupplier) {
        EntityWorld entityWorld = entityWorldSupplier.get();
        Optional<MaxHealthComponent> component = entityWorld.getComponent(event.getEntity(), MaxHealthComponent.class);
        entityWorld.remove(event.getEntity(), MaxHealthComponent.class);
        entityWorld.addComponent(event.getEntity(), new MaxHealthComponent(event.getMaxHealtPoints()));
    }
}
