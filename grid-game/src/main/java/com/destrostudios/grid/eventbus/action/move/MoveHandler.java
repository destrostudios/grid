package com.destrostudios.grid.eventbus.action.move;

import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.EventHandler;
import com.destrostudios.grid.eventbus.Eventbus;
import com.destrostudios.grid.eventbus.update.position.PositionUpdateEvent;
import lombok.AllArgsConstructor;

import java.util.function.Supplier;

@AllArgsConstructor
public class MoveHandler implements EventHandler<MoveEvent> {
    private final Eventbus eventbus;

    @Override
    public void onEvent(MoveEvent event, Supplier<EntityWorld> entityWorldSupplier) {
        eventbus.registerSubEvents(new PositionUpdateEvent(event.getEntity(), event.getPositionComponent()));
    }
}
