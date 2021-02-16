package com.destrostudios.grid.eventbus.handler.properties;

import com.destrostudios.grid.components.properties.MaxAttackPointsComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.events.properties.MaxAttackPointsChangedEvent;
import com.destrostudios.grid.eventbus.handler.EventHandler;

import java.util.function.Supplier;

public class MaxAttackPointsChangedHandler implements EventHandler<MaxAttackPointsChangedEvent> {
    @Override
    public void onEvent(MaxAttackPointsChangedEvent event, Supplier<EntityWorld> entityWorldSupplier) {
        entityWorldSupplier.get().addComponent(event.getEntity(), new MaxAttackPointsComponent(event.getNewMaxAttackPoints()));
    }
}
