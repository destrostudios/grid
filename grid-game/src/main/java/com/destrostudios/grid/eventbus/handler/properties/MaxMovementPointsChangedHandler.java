package com.destrostudios.grid.eventbus.handler.properties;

import com.destrostudios.grid.components.properties.MaxMovementPointsComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.events.PropertiePointsChangedEvent;
import com.destrostudios.grid.eventbus.handler.EventHandler;

import java.util.function.Supplier;

public class MaxMovementPointsChangedHandler implements EventHandler<PropertiePointsChangedEvent.MaxMovementPointsChangedEvent> {
    @Override
    public void onEvent(PropertiePointsChangedEvent.MaxMovementPointsChangedEvent event, Supplier<EntityWorld> entityWorldSupplier) {
        entityWorldSupplier.get().addComponent(event.getEntity(), new MaxMovementPointsComponent(event.getNewPoints()));
    }
}
