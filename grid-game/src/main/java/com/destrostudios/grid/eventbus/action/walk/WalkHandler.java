package com.destrostudios.grid.eventbus.action.walk;

import com.destrostudios.grid.components.properties.MovementPointsComponent;
import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.eventbus.Event;
import com.destrostudios.grid.eventbus.EventHandler;
import com.destrostudios.grid.eventbus.Eventbus;
import com.destrostudios.grid.eventbus.action.move.MoveEvent;
import com.destrostudios.grid.eventbus.update.mp.MovementPointsChangedEvent;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Logger;
import lombok.AllArgsConstructor;

import static com.destrostudios.grid.eventbus.action.move.MoveType.WALK;

// TODO: 01.03.2021 splitten in PositionUpdate handler
@AllArgsConstructor
public class WalkHandler implements EventHandler<WalkEvent> {
    private final static Logger logger = Logger.getLogger(WalkHandler.class.getSimpleName());

    private final Eventbus eventbusInstance;

    @Override
    public void onEvent(WalkEvent componentUpdateEvent, Supplier<EntityData> supplier) {
        int entity = componentUpdateEvent.entity;
        EntityData entityData = supplier.get();

        // update the movementpoints and add new position
        int movementPoints = entityData.getComponent(entity, MovementPointsComponent.class).getMovementPoints();
        List<Event> events = Lists.newArrayList(new MoveEvent(entity, componentUpdateEvent.getPositionComponent(), WALK),
                new MovementPointsChangedEvent(entity, movementPoints - 1));
        eventbusInstance.registerSubEvents(events);
    }
}
