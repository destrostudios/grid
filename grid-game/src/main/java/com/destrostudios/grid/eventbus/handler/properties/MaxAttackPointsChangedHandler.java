package com.destrostudios.grid.eventbus.handler.properties;

import com.destrostudios.grid.components.properties.MaxAttackPointsComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.events.PropertiePointsChangedEvent;
import com.destrostudios.grid.eventbus.handler.EventHandler;

import java.util.function.Supplier;

public class MaxAttackPointsChangedHandler implements EventHandler<PropertiePointsChangedEvent.MaxAttackPointsChangedEvent> {
    @Override
    public void onEvent(PropertiePointsChangedEvent.MaxAttackPointsChangedEvent event, Supplier<EntityWorld> entityWorldSupplier) {
        entityWorldSupplier.get().addComponent(event.getEntity(), new MaxAttackPointsComponent(event.getNewPoints()));
    }
}
