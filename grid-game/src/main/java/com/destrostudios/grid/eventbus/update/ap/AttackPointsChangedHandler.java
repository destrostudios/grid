package com.destrostudios.grid.eventbus.update.ap;

import com.destrostudios.grid.components.properties.AttackPointsComponent;
import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.eventbus.EventHandler;
import java.util.function.Supplier;

public class AttackPointsChangedHandler implements EventHandler<AttackPointsChangedEvent> {
    @Override
    public void onEvent(AttackPointsChangedEvent event, Supplier<EntityData> entityDataSupplier) {
        entityDataSupplier.get().remove(event.getEntity(), AttackPointsComponent.class);
        entityDataSupplier.get().addComponent(event.getEntity(), new AttackPointsComponent(event.getNewPoints()));
    }
}
