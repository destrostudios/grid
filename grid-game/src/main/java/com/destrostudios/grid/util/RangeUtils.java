package com.destrostudios.grid.util;

import com.destrostudios.gametools.grid.LineOfSight;
import com.destrostudios.gametools.grid.Position;
import com.destrostudios.grid.components.map.PositionComponent;
import com.destrostudios.grid.components.map.VisionComponent;
import com.destrostudios.grid.components.map.WalkableComponent;
import com.destrostudios.grid.components.spells.range.CastAreaShape;
import com.destrostudios.grid.components.spells.range.LineOfSightComponent;
import com.destrostudios.grid.components.spells.range.RangeComponent;
import com.destrostudios.grid.entities.EntityData;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.destrostudios.grid.util.SpellUtils.getBaseSquare;

public class RangeUtils {

  public static List<PositionComponent> getRangePosComponents(
      int spellEntity, int casterEntity, EntityData entityData) {
    return getAllTargetableEntitiesInRange(spellEntity, casterEntity, entityData).stream()
        .map(e -> entityData.getComponent(e, PositionComponent.class))
        .collect(Collectors.toList());
  }

  public static List<Integer> getAllTargetableEntitiesInRange(
      int spellEntity, int casterEntity, EntityData entityData) {
    List<Integer> targetableInRange = getAllEntitiesInRange(spellEntity, casterEntity, entityData);
    PositionComponent posCaster = entityData.getComponent(casterEntity, PositionComponent.class);

    List<Integer> result = new ArrayList<>();
    LineOfSight lineOfSight = new LineOfSight();

    if (!entityData.hasComponents(spellEntity, LineOfSightComponent.class)) {
      return targetableInRange;
    }

    Predicate<Position> predicate =
        pos -> {
          PositionComponent positionComponent = new PositionComponent(pos.x, pos.y);
          List<Integer> obstacleEntities =
              entityData.list(VisionComponent.class, PositionComponent.class).stream()
                  .filter(e -> entityData.getComponent(e, VisionComponent.class).isBlockingVision())
                  .toList();
          return obstacleEntities.stream()
              .noneMatch(
                  e ->
                      entityData
                          .getComponent(e, PositionComponent.class)
                          .equals(positionComponent));
        };

    for (Integer entity : targetableInRange) {
      PositionComponent pos = entityData.getComponent(entity, PositionComponent.class);
      if (lineOfSight.inLineOfSight(
          predicate,
          new Position(posCaster.getX(), posCaster.getY()),
          new Position(pos.getX(), pos.getY()))) {
        result.add(entity);
      }
    }
    return result;
  }

  public static List<Integer> getAllEntitiesInRange(
      int spellEntity, int casterEntity, EntityData entityData) {
    List<Integer> targetableInRange = new ArrayList<>();
    RangeComponent rangeComp = entityData.getComponent(spellEntity, RangeComponent.class);
    PositionComponent casterPosition =
        entityData.getComponent(casterEntity, PositionComponent.class);

    Set<PositionComponent> rangablePositions = calculatePositionsInRange(casterPosition, rangeComp);
    for (PositionComponent rangablePosition : rangablePositions) {
      List<Integer> entities = entityData.findEntitiesByComponentValue(rangablePosition);
      for (Integer entity : entities) {
        if (entityData.hasComponents(entity, WalkableComponent.class)) {
          targetableInRange.add(entity);
        }
      }
    }
    return targetableInRange;
  }

  public static Set<PositionComponent> calculatePositionsInRange(
      PositionComponent sourcePos, RangeComponent rangeComponent) {
    CastAreaShape castAreaShape = rangeComponent.getCastAreaShape();
    int maxRange = rangeComponent.getMaxRange();
    int minRange = rangeComponent.getMinRange();
    int yPos = sourcePos.getY();
    int xPos = sourcePos.getX();

    if (castAreaShape == CastAreaShape.SINGLE) {
      return Set.of(sourcePos);
    } else {
      Set<PositionComponent> result = new LinkedHashSet<>(getBaseSquare(sourcePos, maxRange));

      if (castAreaShape == CastAreaShape.PLUS) {
        result.removeIf(pos -> !(pos.getX() == xPos || pos.getY() == yPos));
        getBaseSquare(sourcePos, minRange - 1).forEach(result::remove);

      } else if (castAreaShape == CastAreaShape.DIAMOND) {
        result.removeIf(
            pos ->
                Math.abs(pos.getX() - xPos) + Math.abs(pos.getY() - yPos) > maxRange
                    || Math.abs(pos.getX() - xPos) + Math.abs(pos.getY() - yPos) < minRange);
      }
      return result;
    }
  }
}
