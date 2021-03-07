package com.destrostudios.grid.eventbus.action.move;

import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.eventbus.EventHandler;
import com.destrostudios.grid.eventbus.Eventbus;
import com.destrostudios.grid.eventbus.update.position.PositionUpdateEvent;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MoveHandler implements EventHandler<MoveEvent> {
    private final Eventbus eventbus;

    @Override
    public void onEvent(MoveEvent event, Supplier<EntityData> entityDataSupplier) {
        eventbus.registerSubEvents(new PositionUpdateEvent(event.getEntity(), event.getPositionComponent()));
    }
}
