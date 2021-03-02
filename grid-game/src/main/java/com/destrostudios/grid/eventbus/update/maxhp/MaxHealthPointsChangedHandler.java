package com.destrostudios.grid.eventbus.update.maxhp;

import com.destrostudios.grid.components.properties.MaxHealthComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.EventHandler;

import java.util.function.Supplier;

public class MaxHealthPointsChangedHandler implements EventHandler<MaxHealthPointsChangedEvent> {
    @Override
    public void onEvent(MaxHealthPointsChangedEvent event, Supplier<EntityWorld> entityWorldSupplier) {
        EntityWorld entityWorld = entityWorldSupplier.get();
        entityWorld.remove(event.getEntity(), MaxHealthComponent.class);
        entityWorld.addComponent(event.getEntity(), new MaxHealthComponent(event.getNewPoints()));
    }
}
