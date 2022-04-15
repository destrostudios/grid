package com.destrostudios.grid.eventbus.action.walk;

import com.destrostudios.grid.components.character.ActiveTurnComponent;
import com.destrostudios.grid.components.map.PositionComponent;
import com.destrostudios.grid.components.properties.MovementPointsComponent;
import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.eventbus.EventValidator;

import java.util.function.Supplier;

import static com.destrostudios.grid.util.SpellUtils.isPositionIsFree;

public class WalkValidator implements EventValidator<WalkEvent> {

  @Override
  public boolean validate(WalkEvent componentUpdateEvent, Supplier<EntityData> supplier) {
    EntityData entityData = supplier.get();
    int entity = componentUpdateEvent.getEntity();
    PositionComponent newPosition = componentUpdateEvent.getPositionComponent();

    boolean entityCanMove =
        entityData.hasComponents(
            entity,
            PositionComponent.class,
            MovementPointsComponent.class,
            ActiveTurnComponent.class);
    boolean positionIsFree = isPositionIsFree(entityData, newPosition, entity);
    int neededMovementPoints = getWalkedDistance(entityData, componentUpdateEvent);
    int movementPoints =
        entityData.getComponent(entity, MovementPointsComponent.class).getMovementPoints();
    return positionIsFree && entityCanMove && neededMovementPoints == 1 && movementPoints > 0;
  }

  private int getWalkedDistance(EntityData entityData, WalkEvent componentUpdateEvent) {
    PositionComponent posComp =
        entityData.getComponent(componentUpdateEvent.getEntity(), PositionComponent.class);
    if (posComp == null) {
      return -1;
    }
    PositionComponent updatePositionComponent = componentUpdateEvent.getPositionComponent();
    return Math.abs(updatePositionComponent.getX() - posComp.getX())
        + Math.abs(updatePositionComponent.getY() - posComp.getY());
  }
}
