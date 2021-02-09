package com.destrostudios.grid.update.listener;

import com.destrostudios.grid.components.MovementPointsComponent;
import com.destrostudios.grid.components.PositionComponent;
import com.destrostudios.grid.components.RoundComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.update.eventbus.ComponentUpdateEvent;
import com.destrostudios.grid.update.eventbus.Listener;
import lombok.AllArgsConstructor;

import java.util.Optional;
import java.util.logging.Logger;

@AllArgsConstructor
public class PositionUpdateListener implements Listener<PositionComponent> {
    private final static Logger logger = Logger.getLogger(PositionUpdateListener.class.getSimpleName());


    @Override
    public void handle(ComponentUpdateEvent<PositionComponent> componentUpdateEvent, EntityWorld entityWorld) {
        PositionComponent positionComponent = componentUpdateEvent.getComponent();
        int entity = componentUpdateEvent.getEntity();
        boolean canMove = entityWorld.hasComponents(entity, PositionComponent.class, MovementPointsComponent.class, RoundComponent.class);
        Optional<MovementPointsComponent> mpComponent = entityWorld.getComponent(entity, MovementPointsComponent.class);

        if (canMove && mpComponent.isPresent()) {
            int neededMovementPoints = getNeededMovementPoints(entityWorld, componentUpdateEvent);
            int movementPoints = mpComponent.get().getMovementPoints();

            if (neededMovementPoints > 0 && movementPoints >= neededMovementPoints) {
                // update the movementpoints and add new position
                entityWorld.remove(entity, MovementPointsComponent.class);
                entityWorld.addComponent(entity, new MovementPointsComponent(movementPoints - neededMovementPoints));
                entityWorld.remove(entity, PositionComponent.class);
                entityWorld.addComponent(entity, positionComponent);
                logger.info(String.format("Entity %s moved to (%s|%s) and used %s MP", entity, positionComponent.getX(), positionComponent.getY(),
                        neededMovementPoints));
            }
        }
    }

    private int getNeededMovementPoints(EntityWorld entityWorld, ComponentUpdateEvent<PositionComponent> componentUpdateEvent) {
        Optional<PositionComponent> componentOpt = entityWorld.getComponent(componentUpdateEvent.getEntity(), PositionComponent.class);
        if (componentOpt.isEmpty()) {
            return -1;
        }
        PositionComponent positionComponent = componentOpt.get();
        PositionComponent updatePositionComponent = componentUpdateEvent.getComponent();
        return Math.abs(updatePositionComponent.getX() - positionComponent.getX()) +  Math.abs(updatePositionComponent.getY() - positionComponent.getY());
    }

    @Override
    public Class<PositionComponent> supports() {
        return PositionComponent.class;
    }
}
