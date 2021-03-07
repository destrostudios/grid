package com.destrostudios.grid.eventbus.update.position;

import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.eventbus.EventHandler;
import java.util.function.Supplier;

public class PositionUpdateHandler implements EventHandler<PositionUpdateEvent> {

    @Override
    public void onEvent(PositionUpdateEvent event, Supplier<EntityData> entityDataSupplier) {
        EntityData entityData = entityDataSupplier.get();
        entityData.addComponent(event.getEntity(), event.getNewPosition());
    }
}
