package com.destrostudios.grid.systems;

import com.destrostudios.grid.components.Component;
import com.destrostudios.grid.components.MovingComponent;
import com.destrostudios.grid.components.PlayerComponent;
import com.destrostudios.grid.components.PositionComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.google.common.collect.Iterables;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class MovementSystem implements ComponentSystem {
    private final static Logger logger = Logger.getGlobal();
//
//    @Override
//    public void update(EntityWorld world, ComponentMapObserver componentMapObserver) {
//        for (int updatedEntity : componentMapObserver.getUpdatedEntities()) {
//            final List<Component> updateComponents = componentMapObserver.getAndRemoveUpdates(updatedEntity);
//            final Optional<PositionComponent> positionComponent = world.getComponent(updatedEntity, PositionComponent.class);
//            final Optional<PlayerComponent> playerComponent = world.getComponent(updatedEntity, PlayerComponent.class);
//
//            if (positionComponent.isPresent() && world.hasComponent(updatedEntity, MovingComponent.class)) {
//                List<PositionComponent> positionUpdates = updateComponents.stream() // should just be one
//                        .filter(c -> c instanceof PositionComponent)
//                        .map(c -> (PositionComponent) c)
//                        .collect(Collectors.toList());
//
//                if (!positionUpdates.isEmpty()) {
//                    final PositionComponent newPosition = Iterables.getLast(positionUpdates);
//                    String log = playerComponent.isEmpty()
//                            ? String.format("old position: %s, new position %s", positionComponent, newPosition)
//                            : String.format("player %s moved from old position %s to new position %s", playerComponent.get().getName(), positionComponent, newPosition);
//                    logger.log(Level.INFO, log);
//                    world.addComponent(updatedEntity, newPosition);
//                }
//            }
//        }
//    }
}
