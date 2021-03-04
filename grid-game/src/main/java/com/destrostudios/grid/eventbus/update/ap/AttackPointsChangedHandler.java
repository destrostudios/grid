package com.destrostudios.grid.eventbus.update.ap;

import com.destrostudios.grid.components.properties.AttackPointsComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.EventHandler;

import java.util.function.Supplier;

public class AttackPointsChangedHandler implements EventHandler<AttackPointsChangedEvent> {
    @Override
    public void onEvent(AttackPointsChangedEvent event, Supplier<EntityWorld> entityWorldSupplier) {
        entityWorldSupplier.get().remove(event.getEntity(), AttackPointsComponent.class);
        entityWorldSupplier.get().addComponent(event.getEntity(), new AttackPointsComponent(event.getNewPoints()));
    }
}