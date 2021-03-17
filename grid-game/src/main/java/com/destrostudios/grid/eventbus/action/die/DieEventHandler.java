package com.destrostudios.grid.eventbus.action.die;

import com.destrostudios.grid.components.properties.IsAliveComponent;
import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.eventbus.EventHandler;
import com.destrostudios.grid.util.GameOverInfo;
import lombok.AllArgsConstructor;

import java.util.function.Supplier;

@AllArgsConstructor
public class DieEventHandler implements EventHandler<DieEvent> {
    private final GameOverInfo gameOverInfo;

    @Override
    public void onEvent(DieEvent event, Supplier<EntityData> entityDataSupplier) {
        EntityData entityData = entityDataSupplier.get();
        //  todo
        //  entityData.remove(event.getEntity(), PositionComponent.class);
        entityData.remove(event.getEntity(), IsAliveComponent.class);
        gameOverInfo.checkGameOverStatus();
    }
}
