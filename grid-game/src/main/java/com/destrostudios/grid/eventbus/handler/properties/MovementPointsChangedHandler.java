package com.destrostudios.grid.eventbus.handler.properties;

import com.destrostudios.grid.components.properties.MovementPointsComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.events.properties.MovementPointsChangedEvent;
import com.destrostudios.grid.eventbus.handler.EventHandler;

import java.util.function.Supplier;
import java.util.logging.Logger;

public class MovementPointsChangedHandler implements EventHandler<MovementPointsChangedEvent> {
    private final static Logger logger = Logger.getLogger(MovementPointsChangedHandler.class.getSimpleName());

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
