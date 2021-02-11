package com.destrostudios.grid.eventbus.handler;

import com.destrostudios.grid.components.*;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.Eventbus;
import com.destrostudios.grid.eventbus.events.MovementPointsChangedEvent;
import com.destrostudios.grid.eventbus.events.PositionChangedEvent;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@AllArgsConstructor
public class PositionChangedHandler implements EventHandler<PositionChangedEvent> {
    private final static Logger logger = Logger.getLogger(PositionChangedHandler.class.getSimpleName());

    private final Eventbus eventbusInstance;

    @Override
    public void onEvent(PositionChangedEvent componentUpdateEvent, Supplier<EntityWorld> supplier) {
        logger.info("Processing event " + componentUpdateEvent);
        EntityWorld entityWorld = supplier.get();
        int entity = componentUpdateEvent.getEntity();
        PositionComponent newPosition = componentUpdateEvent.getPositionComponent();

        boolean entityCanMove = entityWorld.hasComponents(entity, PositionComponent.class, MovementPointsComponent.class, RoundComponent.class);
        boolean positionIsFree = isPositionIsFree(entityWorld, newPosition, entity);

        int neededMovementPoints = getWalkedDistance(entityWorld, componentUpdateEvent);
        int movementPoints = entityWorld.getComponent(entity, MovementPointsComponent.class).get().getMovementPoints();

        if (positionIsFree && entityCanMove && neededMovementPoints == 1 && movementPoints > 0) {
            // update the movementpoints and add new position

            entityWorld.remove(entity, PositionComponent.class);
            entityWorld.addComponent(entity, newPosition);
            logger.info(String.format("Entity %s moved to (%s|%s)", entity, newPosition.getX(), newPosition.getY()));
            eventbusInstance.triggerEvent(new MovementPointsChangedEvent(entity, movementPoints - 1));
        }
    }

    private boolean isPositionIsFree(EntityWorld entityWorld, PositionComponent newPosition, int entity) {
        List<Integer> allPlayersEntites = entityWorld.list(PositionComponent.class, PlayerComponent.class).stream()
                .filter(e -> e != entity)
                .collect(Collectors.toList());
        List<Integer> allTreeEntites = entityWorld.list(PositionComponent.class, TreeComponent.class).stream()
                .filter(e -> e != entity)
                .collect(Collectors.toList());
        List<Integer> allWalkableEntities = entityWorld.list(PositionComponent.class, WalkableComponent.class).stream()
                .filter(e -> e != entity)
                .collect(Collectors.toList());

        boolean collidesWithOtherPlayer = allPlayersEntites.stream().anyMatch(pE -> newPosition.equals(entityWorld.getComponent(pE, PositionComponent.class).get()));
        boolean collidesWithTree = allTreeEntites.stream().anyMatch(pE -> newPosition.equals(entityWorld.getComponent(pE, PositionComponent.class).get()));
        boolean isWalkableField = allWalkableEntities.stream().anyMatch(pE -> newPosition.equals(entityWorld.getComponent(pE, PositionComponent.class).get()));

        return isWalkableField && !collidesWithOtherPlayer && !collidesWithTree;
    }

    private int getWalkedDistance(EntityWorld entityWorld, PositionChangedEvent componentUpdateEvent) {
        Optional<PositionComponent> componentOpt = entityWorld.getComponent(componentUpdateEvent.getEntity(), PositionComponent.class);
        if (componentOpt.isEmpty()) {
            return -1;
        }
        PositionComponent positionComponent = componentOpt.get();
        PositionComponent updatePositionComponent = componentUpdateEvent.getPositionComponent();
        return Math.abs(updatePositionComponent.getX() - positionComponent.getX()) + Math.abs(updatePositionComponent.getY() - positionComponent.getY());
    }


    @Override
    public Class<PositionChangedEvent> getEventClass() {
        return PositionChangedEvent.class;
    }
}
