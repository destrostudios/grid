package com.destrostudios.grid.eventbus.handler;

import com.destrostudios.grid.components.properties.MovementPointsComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.Eventbus;
import com.destrostudios.grid.eventbus.events.MovementPointsChangedEvent;
import lombok.AllArgsConstructor;

import java.util.function.Supplier;
import java.util.logging.Logger;

@AllArgsConstructor
public class MovementPointsChangedHandler implements EventHandler<MovementPointsChangedEvent> {
    private final static Logger logger = Logger.getLogger(MovementPointsChangedHandler.class.getSimpleName());

    private final Eventbus eventbusInstance;

    @Override
    public void onEvent(MovementPointsChangedEvent event, Supplier<EntityWorld> entityWorldSupplier) {
        logger.info("Processing event " + event);
        int entity = event.getEntity();
        EntityWorld entityWorld = entityWorldSupplier.get();
        MovementPointsComponent movementPointsComponent = entityWorld.getComponent(entity, MovementPointsComponent.class).get();
        entityWorld.remove(entity, MovementPointsComponent.class);
        entityWorld.addComponent(entity, new MovementPointsComponent(event.getMovementPoints()));
        logger.info(String.format("MP of %s set from %s to %s", entity, movementPointsComponent.getMovementPoints(), event.getMovementPoints()));
    }
}
