package com.destrostudios.grid.eventbus.action.move;

import com.destrostudios.grid.components.properties.MovementPointsComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.Event;
import com.destrostudios.grid.eventbus.EventHandler;
import com.destrostudios.grid.eventbus.Eventbus;
import com.destrostudios.grid.eventbus.update.mp.MovementPointsChangedEvent;
import com.destrostudios.grid.eventbus.update.position.PositionUpdateEvent;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Logger;

// TODO: 01.03.2021 splitten in PositionUpdate handler
@AllArgsConstructor
public class MoveHandler implements EventHandler<MoveEvent> {
    private final static Logger logger = Logger.getLogger(MoveHandler.class.getSimpleName());

    private final Eventbus eventbusInstance;

    @Override
    public void onEvent(MoveEvent componentUpdateEvent, Supplier<EntityWorld> supplier) {
        int entity = componentUpdateEvent.entity;
        EntityWorld entityWorld = supplier.get();

        // update the movementpoints and add new position
        int movementPoints = entityWorld.getComponent(entity, MovementPointsComponent.class).getMovementPoints();
        List<Event> events = Lists.newArrayList(new PositionUpdateEvent(entity, componentUpdateEvent.getPositionComponent()),
                new MovementPointsChangedEvent(entity, movementPoints - 1));
        eventbusInstance.registerSubEvents(events);
    }
}
