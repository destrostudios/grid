package com.destrostudios.grid.eventbus.handler.properties;

import com.destrostudios.grid.components.properties.MaxHealthComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.events.PropertiePointsChangedEvent;
import com.destrostudios.grid.eventbus.handler.EventHandler;

import java.util.function.Supplier;

public class MaxHealthPointsChangedHandler implements EventHandler<PropertiePointsChangedEvent.MaxHealthPointsChangedEvent> {
    @Override
    public void onEvent(PropertiePointsChangedEvent.MaxHealthPointsChangedEvent event, Supplier<EntityWorld> entityWorldSupplier) {
        EntityWorld entityWorld = entityWorldSupplier.get();
        entityWorld.remove(event.getEntity(), MaxHealthComponent.class);
        entityWorld.addComponent(event.getEntity(), new MaxHealthComponent(event.getEntity()));
    }
}
