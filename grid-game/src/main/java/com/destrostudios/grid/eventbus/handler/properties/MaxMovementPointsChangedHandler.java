package com.destrostudios.grid.eventbus.handler.properties;

import com.destrostudios.grid.components.properties.MaxMovementPointsComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.events.properties.MaxMovementPointsChangedEvent;
import com.destrostudios.grid.eventbus.handler.EventHandler;

import java.util.function.Supplier;

public class MaxMovementPointsChangedHandler implements EventHandler<MaxMovementPointsChangedEvent> {
    @Override
    public void onEvent(MaxMovementPointsChangedEvent event, Supplier<EntityWorld> entityWorldSupplier) {
        entityWorldSupplier.get().addComponent(event.getEntity(), new MaxMovementPointsComponent(event.getMaxMovementPoints()));
    }
}
