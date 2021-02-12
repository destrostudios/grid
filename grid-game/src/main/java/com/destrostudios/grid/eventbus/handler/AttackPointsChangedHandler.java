package com.destrostudios.grid.eventbus.handler;

import com.destrostudios.grid.components.AttackPointsComponent;
import com.destrostudios.grid.components.spells.AttackPointCostComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.events.AttackPointsChangedEvent;

import java.util.function.Supplier;

public class AttackPointsChangedHandler implements EventHandler<AttackPointsChangedEvent> {
    @Override
    public void onEvent(AttackPointsChangedEvent event, Supplier<EntityWorld> entityWorldSupplier) {
        entityWorldSupplier.get().remove(event.getEntity(), AttackPointsComponent.class);
        entityWorldSupplier.get().addComponent(event.getEntity(), new AttackPointsComponent(event.getNewAttackPoints()));
    }
}
