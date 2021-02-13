package com.destrostudios.grid.eventbus.validator;

import com.destrostudios.grid.components.*;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.events.MoveEvent;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MoveValidator implements EventValidator<MoveEvent> {

    @Override
    public boolean validate(MoveEvent componentUpdateEvent, Supplier<EntityWorld> supplier) {
        EntityWorld entityWorld = supplier.get();
        int entity = componentUpdateEvent.getEntity();
        PositionComponent newPosition = componentUpdateEvent.getPositionComponent();

        boolean entityCanMove = entityWorld.hasComponents(entity, PositionComponent.class, MovementPointsComponent.class, RoundComponent.class);
        boolean positionIsFree = isPositionIsFree(entityWorld, newPosition, entity);
        int neededMovementPoints = getWalkedDistance(entityWorld, componentUpdateEvent);
        int movementPoints = entityWorld.getComponent(entity, MovementPointsComponent.class).get().getMovementPoints();

        return positionIsFree && entityCanMove && neededMovementPoints == 1 && movementPoints > 0;
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

    private int getWalkedDistance(EntityWorld entityWorld, MoveEvent componentUpdateEvent) {
        Optional<PositionComponent> componentOpt = entityWorld.getComponent(componentUpdateEvent.getEntity(), PositionComponent.class);
        if (componentOpt.isEmpty()) {
            return -1;
        }
        PositionComponent positionComponent = componentOpt.get();
        PositionComponent updatePositionComponent = componentUpdateEvent.getPositionComponent();
        return Math.abs(updatePositionComponent.getX() - positionComponent.getX()) + Math.abs(updatePositionComponent.getY() - positionComponent.getY());
    }
}
