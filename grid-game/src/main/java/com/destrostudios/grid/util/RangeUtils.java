package com.destrostudios.grid.util;

import com.destrostudios.grid.components.character.PlayerComponent;
import com.destrostudios.grid.components.map.ObstacleComponent;
import com.destrostudios.grid.components.map.PositionComponent;
import com.destrostudios.grid.components.map.WalkableComponent;
import com.destrostudios.grid.components.properties.AttackPointsComponent;
import com.destrostudios.grid.components.properties.BuffsComponent;
import com.destrostudios.grid.components.properties.HealthPointsComponent;
import com.destrostudios.grid.components.properties.MovementPointsComponent;
import com.destrostudios.grid.components.spells.buffs.BuffComponent;
import com.destrostudios.grid.components.spells.limitations.CostComponent;
import com.destrostudios.grid.components.spells.range.AffectedAreaComponent;
import com.destrostudios.grid.components.spells.range.AreaShape;
import com.destrostudios.grid.components.spells.range.RangeComponent;
import com.destrostudios.grid.components.spells.range.RangeIndicator;
import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.eventbus.action.displace.Direction;
import com.destrostudios.turnbasedgametools.grid.LineOfSight;
import com.destrostudios.turnbasedgametools.grid.Position;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RangeUtils {
    // todo 3.) targetable component
    // todo 5.) Tooltips automatisch generieren
    // todo 6.) AP/MP steal components
    // todo 9.) position swap


    public static List<Integer> getAllTargetableEntitiesInRange(int spellEntity, int casterEntity, EntityData entityData) {
        List<Integer> targetableInRange = getAllEntitiesInRange(spellEntity, casterEntity, entityData);
        PositionComponent posCaster = entityData.getComponent(casterEntity, PositionComponent.class);

        List<Integer> result = new ArrayList<>();
        LineOfSight lineOfSight = new LineOfSight();

        RangeComponent component = entityData.getComponent(spellEntity, RangeComponent.class);
        if (component.getRangeIndicator() == RangeIndicator.ALL) {
            return targetableInRange;
        }

        Predicate<Position> predicate = pos -> {
            PositionComponent positionComponent = new PositionComponent(pos.x, pos.y);
            List<Integer> obstacleEntities = entityData.list(ObstacleComponent.class);
            return obstacleEntities.stream()
                    .noneMatch(e -> entityData.getComponent(e, PositionComponent.class).equals(positionComponent));
        };

        for (Integer entity : targetableInRange) {
            PositionComponent pos = entityData.getComponent(entity, PositionComponent.class);
            if (lineOfSight.inLineOfSight(predicate, new Position(posCaster.getX(), posCaster.getY()), new Position(pos.getX(), pos.getY()))) {
                result.add(entity);
            }
        }
        return result;
    }

    public static List<Integer> getAllEntitiesInRange(int spellEntity, int casterEntity, EntityData entityData) {
        List<Integer> targetableInRange = new ArrayList<>();
        RangeComponent rangeComponentOpt = entityData.getComponent(spellEntity, RangeComponent.class);
        PositionComponent casterPositionOpt = entityData.getComponent(casterEntity, PositionComponent.class);
        int maxRange = rangeComponentOpt.getMaxRange();
        int minRange = rangeComponentOpt.getMinRange();
        AreaShape area = rangeComponentOpt.getRangeShape();

        Set<PositionComponent> rangablePositions = calculateAffectedPositions(null, casterPositionOpt, new AffectedAreaComponent(area, minRange, maxRange));

        List<Integer> walkableAndTargetablePos = entityData.list(PositionComponent.class, WalkableComponent.class);

        for (int walkableAndTargetablePo : walkableAndTargetablePos) {
            PositionComponent posC = entityData.getComponent(walkableAndTargetablePo, PositionComponent.class);
            if (rangablePositions.contains(posC)) {
                targetableInRange.add(walkableAndTargetablePo);
            }
        }
        return targetableInRange;
    }

    public static List<Integer> getAffectedPlayerEntities(int spellEntity, PositionComponent sourcePos, PositionComponent clickedPos, EntityData entityData) {
        Set<PositionComponent> positionComponents = calculateAffectedPositions(sourcePos, clickedPos, entityData.getComponent(spellEntity, AffectedAreaComponent.class));
        return entityData.list(PlayerComponent.class).stream()
                .filter(e -> positionComponents.contains(entityData.getComponent(e, PositionComponent.class)))
                .collect(Collectors.toList());
    }


    public static List<Integer> getAffectedWalkableEntities(int spellEntity, PositionComponent sourcePos, PositionComponent clickedPos, EntityData entityData) {
        Set<PositionComponent> positionComponents = calculateAffectedPositions(sourcePos, clickedPos, entityData.getComponent(spellEntity, AffectedAreaComponent.class));
        return entityData.list(WalkableComponent.class).stream()
                .filter(e -> positionComponents.contains(entityData.getComponent(e, PositionComponent.class)))
                .collect(Collectors.toList());
    }


    public static Set<PositionComponent> calculateAffectedPositions(PositionComponent sourcePos, PositionComponent clickedPos, AffectedAreaComponent component) {
        Set<PositionComponent> result = new LinkedHashSet<>();
        if (component == null) {
            return result;
        }

        int maxImpact = component.getMaxImpact();
        int minImpact = component.getMinImpact();
        int yPos = clickedPos.getY();
        int xPos = clickedPos.getX();

        if (component.getShape() == AreaShape.SINGLE) {
            return Set.of(clickedPos);

        } else if (component.getShape() == AreaShape.LINE) {
            return calculateAffectedPosEntitiesForLine(sourcePos, clickedPos, maxImpact, minImpact, yPos, xPos);

        } else if (component.getShape() == AreaShape.DIAMON_SELFCAST && sourcePos.equals(clickedPos)) {
            result.addAll(getBaseSquare(sourcePos, maxImpact));
            result.removeIf(pos -> Math.abs(pos.getX() - sourcePos.getX()) + Math.abs(pos.getY() - sourcePos.getY()) > maxImpact);
            result.removeIf(pos -> Math.abs(pos.getX() - sourcePos.getX()) <= minImpact || Math.abs(pos.getY() - sourcePos.getY()) <= minImpact);

        } else {
            result.addAll(getBaseSquare(clickedPos, maxImpact));

            if (component.getShape() == AreaShape.PLUS) {
                result.removeIf(pos -> !(pos.getX() == xPos || pos.getY() == yPos));
                result.removeAll(getBaseSquare(clickedPos, minImpact - 1));

            } else if (component.getShape() == AreaShape.DIAMOND) {
                result.removeIf(pos -> Math.abs(pos.getX() - xPos) + Math.abs(pos.getY() - yPos) > maxImpact
                        || Math.abs(pos.getX() - xPos) + Math.abs(pos.getY() - yPos) < minImpact);
            } else if (component.getShape() == AreaShape.SQUARE) {
                result.removeAll(getBaseSquare(clickedPos, minImpact - 1));
            }
        }
        return result;
    }

    private static List<PositionComponent> getBaseSquare(PositionComponent pos, int impact) {
        // add big square
        List<PositionComponent> resultTmp = new ArrayList<>();

        for (int y = pos.getY() - impact; y <= pos.getY() + impact; y++) {
            for (int x = pos.getX() - impact; x <= pos.getX() + impact; x++) {
                resultTmp.add(new PositionComponent(x, y));
            }
        }
        return resultTmp;
    }

    private static Set<PositionComponent> calculateAffectedPosEntitiesForLine(PositionComponent sourcePos, PositionComponent clickedPos, int maxImpact, int minImpact, int yPos, int xPos) {
        Set<PositionComponent> result = new LinkedHashSet<>();
        int signumY = (int) Math.signum(clickedPos.getY() - sourcePos.getY());
        Function<Integer, Boolean> testY = signumY < 0
                ? y -> y >= yPos - maxImpact
                : y -> y <= yPos + maxImpact;
        int signumX = (int) Math.signum(clickedPos.getX() - sourcePos.getX());
        Function<Integer, Boolean> testX = signumX < 0
                ? x -> x >= xPos - maxImpact
                : x -> x <= xPos + maxImpact;

        if (Math.abs(sourcePos.getX() - clickedPos.getX()) < Math.abs(sourcePos.getY() - clickedPos.getY())) {
            int signum = (int) Math.signum(clickedPos.getY() - sourcePos.getY());

            for (int y = yPos + signumX * minImpact; testY.apply(y); y += signum) {
                result.add(new PositionComponent(xPos, y));
            }

        } else if (Math.abs(sourcePos.getX() - clickedPos.getX()) > Math.abs(sourcePos.getY() - clickedPos.getY())) {
            // from left or right
            for (int x = xPos + signumX * minImpact; testX.apply(x); x += signumX) {
                result.add(new PositionComponent(x, yPos));
            }

        } else if (signumY != 0 && signumX != 0) {
            // diagonal
            for (int x = xPos + signumX * minImpact, y = yPos + signumY * minImpact; testX.apply(x) && testY.apply(y); x += signumX, y += signumY) {
                result.add(new PositionComponent(x, y));
            }
        }
        return result;
    }

    public static int calculateTargetEntity(int x, int y, EntityData data) {
        Optional<Integer> targetEntity = data.list(PositionComponent.class).stream()
                .filter(e -> data.getComponent(e, PositionComponent.class).getX() == x
                        && data.getComponent(e, PositionComponent.class).getY() == y)
                .min((e1, e2) -> Boolean.compare(data.hasComponents(e2, PlayerComponent.class), data.hasComponents(e1, PlayerComponent.class)));
        return targetEntity.orElse(-1);
    }

    public static <E extends BuffComponent> int getBuffAmount(int spellEntity, int playerEntity, EntityData data, Class<E> classz) {
        int buffPlayer = data.hasComponents(playerEntity, classz)
                ? data.getComponent(playerEntity, classz).getBuffAmount()
                : 0;
        List<Integer> spellBuffEntities = data.hasComponents(spellEntity, BuffsComponent.class)
                ? data.getComponent(spellEntity, BuffsComponent.class).getBuffEntities()
                : new ArrayList<>();
        int buffSpell = spellBuffEntities.stream()
                .flatMap(spellBuff -> data.getComponents(spellBuff).stream())
                .filter(classz::isInstance)
                .map(spellBuff -> (BuffComponent) spellBuff)
                .mapToInt(BuffComponent::getBuffAmount)
                .sum();
        return buffPlayer + buffSpell;
    }

    public static List<PositionComponent> getRangePosComponents(int spellEntity, int casterEntity, EntityData entityData) {
        return getAllTargetableEntitiesInRange(spellEntity, casterEntity, entityData).stream()
                .map(e -> entityData.getComponent(e, PositionComponent.class))
                .collect(Collectors.toList());
    }

    public static boolean isPositionIsFree(EntityData entityData, PositionComponent newPosition, int entity) {
        // TODO: do we really need entity here? it will only block itself if we try to move it to its own position.
        List<Integer> allPlayersEntites = entityData.list(PositionComponent.class, PlayerComponent.class).stream()
                .filter(e -> e != entity)
                .collect(Collectors.toList());
        List<Integer> allObstacleEntites = entityData.list(PositionComponent.class, ObstacleComponent.class).stream()
                .filter(e -> e != entity)
                .collect(Collectors.toList());
        List<Integer> allWalkableEntities = entityData.list(PositionComponent.class, WalkableComponent.class).stream()
                .filter(e -> e != entity)
                .collect(Collectors.toList());

        boolean collidesWithOtherPlayer = allPlayersEntites.stream().anyMatch(pE -> newPosition.equals(entityData.getComponent(pE, PositionComponent.class)));
        boolean collidesWithObstacle = allObstacleEntites.stream().anyMatch(pE -> newPosition.equals(entityData.getComponent(pE, PositionComponent.class)));
        boolean isWalkableField = allWalkableEntities.stream().anyMatch(pE -> newPosition.equals(entityData.getComponent(pE, PositionComponent.class)));

        return isWalkableField && !collidesWithOtherPlayer && !collidesWithObstacle;
    }

    public static PositionComponent getDisplacementGoal(EntityData entityData, int entity, PositionComponent from, Direction direction, int steps) {
        PositionComponent result = from;
        for (int i = 1; i <= steps; i++) {
            PositionComponent next = new PositionComponent(from.getX() + i * direction.getDeltaX(), from.getY() + i * direction.getDeltaY());
            if (!isPositionIsFree(entityData, next, entity)) {
                break;
            }
            result = next;
        }
        return result;
    }

    public static boolean isCastable(int casterEntity, int spellEntity, EntityData entityData) {
        return isCostPayable(casterEntity, spellEntity, entityData); // todo max turn
    }

    public static boolean isCostPayable(int casterEntity, int spellEntity, EntityData entityData) {
        CostComponent cost = entityData.getComponent(spellEntity, CostComponent.class);
        AttackPointsComponent apComp = entityData.getComponent(casterEntity, AttackPointsComponent.class);
        MovementPointsComponent mpComp = entityData.getComponent(casterEntity, MovementPointsComponent.class);
        HealthPointsComponent hpComp = entityData.getComponent(casterEntity, HealthPointsComponent.class);
        return apComp.getAttackPoints() >= cost.getApCost() && mpComp.getMovementPoints() >= cost.getMpCost() && hpComp.getHealth() >= cost.getHpCost();
    }

    public static Direction directionForDelta(PositionComponent from, PositionComponent to) {
        return directionForDelta(to.getX() - from.getX(), to.getY() - from.getY());
    }

    public static Direction directionForDelta(int deltaX, int deltaY) {
        if (Math.abs(deltaX) > Math.abs(deltaY)) {
            deltaY = 0;
        } else if (Math.abs(deltaX) < Math.abs(deltaY)) {
            deltaX = 0;
        }

        int signX = Integer.signum(deltaX);
        int signY = Integer.signum(deltaY);
        for (Direction direction : Direction.values()) {
            if (direction.getDeltaX() == signX && direction.getDeltaY() == signY) {
                return direction;
            }
        }
        throw new AssertionError();
    }

}
