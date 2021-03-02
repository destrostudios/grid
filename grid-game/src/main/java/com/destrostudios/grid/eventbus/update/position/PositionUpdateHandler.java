package com.destrostudios.grid.eventbus.update.position;

import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.EventHandler;

import java.util.function.Supplier;

public class PositionUpdateHandler implements EventHandler<PositionUpdateEvent> {

    @Override
    public void onEvent(PositionUpdateEvent event, Supplier<EntityWorld> entityWorldSupplier) {
        EntityWorld entityWorld = entityWorldSupplier.get();
        entityWorld.addComponent(event.getEntity(), event.getNewPosition());
    }
}
