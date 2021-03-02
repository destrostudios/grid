package com.destrostudios.grid.eventbus.action.move;

import com.destrostudios.grid.components.character.TurnComponent;
import com.destrostudios.grid.components.map.PositionComponent;
import com.destrostudios.grid.components.properties.MovementPointsComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.EventValidator;

import java.util.function.Supplier;

import static com.destrostudios.grid.util.CalculationUtils.isPositionIsFree;

public class MoveValidator implements EventValidator<MoveEvent> {

    @Override
    public boolean validate(MoveEvent componentUpdateEvent, Supplier<EntityWorld> supplier) {
        EntityWorld entityWorld = supplier.get();
        int entity = componentUpdateEvent.getEntity();
        PositionComponent newPosition = componentUpdateEvent.getPositionComponent();

        boolean entityCanMove = entityWorld.hasComponents(entity, PositionComponent.class, MovementPointsComponent.class, TurnComponent.class);
        boolean positionIsFree = isPositionIsFree(entityWorld, newPosition, entity);
        int neededMovementPoints = getWalkedDistance(entityWorld, componentUpdateEvent);
        int movementPoints = entityWorld.getComponent(entity, MovementPointsComponent.class).getMovementPoints();
        return positionIsFree && entityCanMove && neededMovementPoints == 1 && movementPoints > 0;
    }


    private int getWalkedDistance(EntityWorld entityWorld, MoveEvent componentUpdateEvent) {
        PositionComponent posComp = entityWorld.getComponent(componentUpdateEvent.getEntity(), PositionComponent.class);
        if (posComp == null) {
            return -1;
        }
        PositionComponent updatePositionComponent = componentUpdateEvent.getPositionComponent();
        return Math.abs(updatePositionComponent.getX() - posComp.getX()) + Math.abs(updatePositionComponent.getY() - posComp.getY());
    }
}
