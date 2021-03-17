package com.destrostudios.grid.eventbus.action.swap;

import com.destrostudios.grid.components.map.PositionComponent;
import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.eventbus.EventHandler;
import com.destrostudios.grid.eventbus.Eventbus;
import lombok.AllArgsConstructor;

import java.util.function.Supplier;

@AllArgsConstructor
public class SwapEventHandler implements EventHandler<SwapEvent> {

    @Override
    public void onEvent(SwapEvent event, Supplier<EntityData> entityDataSupplier) {
        EntityData world = entityDataSupplier.get();
        PositionComponent sourcePos = world.getComponent(event.getSourceEntity(), PositionComponent.class);
        PositionComponent targetPos = world.getComponent(event.getTargetEntity(), PositionComponent.class);

        world.addComponent(event.getTargetEntity(), sourcePos);
        world.addComponent(event.getSourceEntity(), targetPos);
    }
}
