package com.destrostudios.grid.eventbus.update.maxmp;

import com.destrostudios.grid.components.properties.MaxMovementPointsComponent;
import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.eventbus.EventHandler;
import java.util.function.Supplier;

public class MaxMovementPointsChangedHandler implements EventHandler<MaxMovementPointsChangedEvent> {
    @Override
    public void onEvent(MaxMovementPointsChangedEvent event, Supplier<EntityData> entityDataSupplier) {
        entityDataSupplier.get().addComponent(event.getEntity(), new MaxMovementPointsComponent(event.getNewPoints()));
    }
}
