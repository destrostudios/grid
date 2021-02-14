package com.destrostudios.grid.eventbus.handler;

import com.destrostudios.grid.components.character.PlayerComponent;
import com.destrostudios.grid.components.map.PositionComponent;
import com.destrostudios.grid.components.map.TreeComponent;
import com.destrostudios.grid.components.map.WalkableComponent;
import com.destrostudios.grid.components.properties.MovementPointsComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.Eventbus;
import com.destrostudios.grid.eventbus.events.MoveEvent;
import com.destrostudios.grid.eventbus.events.MovementPointsChangedEvent;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@AllArgsConstructor
public class PositionChangedHandler implements EventHandler<MoveEvent> {
    private final static Logger logger = Logger.getLogger(PositionChangedHandler.class.getSimpleName());

    private final Eventbus eventbusInstance;

    @Override
    public void onEvent(MoveEvent componentUpdateEvent, Supplier<EntityWorld> supplier) {
        int entity = componentUpdateEvent.entity;
        EntityWorld entityWorld = supplier.get();
        PositionComponent newPosition = componentUpdateEvent.getPositionComponent();
        // update the movementpoints and add new position
        entityWorld.remove(entity, PositionComponent.class);
        entityWorld.addComponent(entity, newPosition);
        int movementPoints = entityWorld.getComponent(entity, MovementPointsComponent.class).get().getMovementPoints();

        logger.info(String.format("Entity %s moved to (%s|%s)", entity, newPosition.getX(), newPosition.getY()));
        eventbusInstance.registerSubEvents(new MovementPointsChangedEvent(entity, movementPoints - 1));
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
