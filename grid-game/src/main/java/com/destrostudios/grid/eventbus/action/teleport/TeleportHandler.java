package com.destrostudios.grid.eventbus.action.teleport;

import com.destrostudios.grid.components.map.PositionComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.EventHandler;
import com.destrostudios.grid.eventbus.Eventbus;
import com.destrostudios.grid.eventbus.update.position.PositionUpdateEvent;
import lombok.AllArgsConstructor;

import java.util.function.Supplier;

@AllArgsConstructor
public class TeleportHandler implements EventHandler<TeleportEvent> {
    private final Eventbus eventbus;

    @Override
    public void onEvent(TeleportEvent event, Supplier<EntityWorld> entityWorldSupplier) {
        eventbus.registerSubEvents(new PositionUpdateEvent(event.getEntity(), new PositionComponent(event.getX(), event.getY())));
    }
}
