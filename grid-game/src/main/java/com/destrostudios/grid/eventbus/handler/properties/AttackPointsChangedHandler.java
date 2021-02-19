package com.destrostudios.grid.eventbus.handler.properties;

import com.destrostudios.grid.components.properties.AttackPointsComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.events.PropertiePointsChangedEvent;
import com.destrostudios.grid.eventbus.handler.EventHandler;

import java.util.function.Supplier;

public class AttackPointsChangedHandler implements EventHandler<PropertiePointsChangedEvent.AttackPointsChangedEvent> {
    @Override
    public void onEvent(PropertiePointsChangedEvent.AttackPointsChangedEvent event, Supplier<EntityWorld> entityWorldSupplier) {
        entityWorldSupplier.get().remove(event.getEntity(), AttackPointsComponent.class);
        entityWorldSupplier.get().addComponent(event.getEntity(), new AttackPointsComponent(event.getNewPoints()));
    }
}
