package com.destrostudios.grid.eventbus.listener;

import com.destrostudios.grid.components.MovementPointsComponent;
import com.destrostudios.grid.components.PositionComponent;
import com.destrostudios.grid.components.RoundComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.ComponentUpdateEvent;
import com.destrostudios.grid.eventbus.Listener;
import lombok.AllArgsConstructor;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Logger;

@AllArgsConstructor
public class PositionUpdateListener implements Listener<PositionComponent> {
    private final static Logger logger = Logger.getLogger(PositionUpdateListener.class.getSimpleName());

    @Override
    public void handle(ComponentUpdateEvent<PositionComponent> componentUpdateEvent, EntityWorld entityWorld) {
        int entity = componentUpdateEvent.getEntity();
        PositionComponent newPosition = componentUpdateEvent.getComponent();
        PositionComponent oldPosition = entityWorld.getComponent(entity, PositionComponent.class).get();

        boolean canMove = entityWorld.hasComponents(entity, PositionComponent.class, MovementPointsComponent.class, RoundComponent.class);

        Optional<MovementPointsComponent> mpComponent = entityWorld.getComponent(entity, MovementPointsComponent.class);

        if (canMove && isPositionIsFree(entityWorld, newPosition) && mpComponent.isPresent()) {
            int neededMovementPoints = getNeededMovementPoints(entityWorld, componentUpdateEvent);
            int movementPoints = mpComponent.get().getMovementPoints();

            if (neededMovementPoints > 0 && movementPoints >= neededMovementPoints) {
                // update the movementpoints and add new position
                entityWorld.remove(entity, MovementPointsComponent.class);
                entityWorld.addComponent(entity, new MovementPointsComponent(movementPoints - neededMovementPoints));
                entityWorld.remove(entity, PositionComponent.class);
                entityWorld.addComponent(entity, newPosition);
                logger.info(String.format("Entity %s moved to (%s|%s) and used %s MP", entity, newPosition.getX(), newPosition.getY(),
                        neededMovementPoints));
            }
        }
    }

    private boolean isPositionIsFree(EntityWorld entityWorld, PositionComponent newPosition) {
        return entityWorld.listComponents(PositionComponent.class).stream()
                .noneMatch(pc -> pc.getX() == newPosition.getX() && pc.getY() == newPosition.getY());
    }

    private int getNeededMovementPoints(EntityWorld entityWorld, ComponentUpdateEvent<PositionComponent> componentUpdateEvent) {
        Optional<PositionComponent> componentOpt = entityWorld.getComponent(componentUpdateEvent.getEntity(), PositionComponent.class);
        if (componentOpt.isEmpty()) {
            return -1;
        }
        PositionComponent positionComponent = componentOpt.get();
        PositionComponent updatePositionComponent = componentUpdateEvent.getComponent();
        return Math.abs(updatePositionComponent.getX() - positionComponent.getX()) + Math.abs(updatePositionComponent.getY() - positionComponent.getY());
    }

    @Override
    public Class<PositionComponent> supports() {
        return PositionComponent.class;
    }
}
