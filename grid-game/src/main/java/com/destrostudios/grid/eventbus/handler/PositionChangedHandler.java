package com.destrostudios.grid.eventbus.handler;

import com.destrostudios.grid.components.properties.MovementPointsComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.Eventbus;
import com.destrostudios.grid.eventbus.events.MoveEvent;
import com.destrostudios.grid.eventbus.events.PropertiePointsChangedEvent;
import lombok.AllArgsConstructor;

import java.util.function.Supplier;
import java.util.logging.Logger;

@AllArgsConstructor
public class PositionChangedHandler implements EventHandler<MoveEvent> {
    private final static Logger logger = Logger.getLogger(PositionChangedHandler.class.getSimpleName());

    private final Eventbus eventbusInstance;

    @Override
    public void onEvent(MoveEvent componentUpdateEvent, Supplier<EntityWorld> supplier) {
        int entity = componentUpdateEvent.entity;
        EntityWorld entityWorld = supplier.get();

        // update the movementpoints and add new position
        entityWorld.addComponent(entity,  componentUpdateEvent.getPositionComponent());
        int movementPoints = entityWorld.getComponent(entity, MovementPointsComponent.class).getMovementPoints();
        eventbusInstance.registerSubEvents(new PropertiePointsChangedEvent.MovementPointsChangedEvent(entity, movementPoints - 1));
    }
}
