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
import com.destrostudios.grid.components.spells.range.AffectedAreaIndicator;
import com.destrostudios.grid.components.spells.range.RangeComponent;
import com.destrostudios.grid.components.spells.range.RangeIndicator;
import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.turnbasedgametools.grid.LineOfSight;
import com.destrostudios.turnbasedgametools.grid.Position;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RangeUtils {
    // todo 3.) targatable component
    // todo 5.) Tooltips automatisch generieren
    // todo 6.) raub components
    // todo 8.) push / pull @Phil
    // todo 9.) swap


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
        int x = casterPositionOpt.getX();
        int y = casterPositionOpt.getY();
        List<Integer> walkableAndTargetablePos = entityData.list(PositionComponent.class, WalkableComponent.class);

        for (int walkableAndTargetablePo : walkableAndTargetablePos) {
            PositionComponent posC = entityData.getComponent(walkableAndTargetablePo, PositionComponent.class);
            if (Math.abs(posC.getX() - x) + Math.abs(posC.getY() - y) <= maxRange
                    && Math.abs(posC.getX() - x) + Math.abs(posC.getY() - y) >= minRange) {
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
        Set<PositionComponent> result = new HashSet<>();
        if (component == null) {
            return result;
        }

        int maxImpact = component.getMaxImpact();
        int minImpact = component.getMinImpact();
        int yPos = clickedPos.getY();
        int xPos = clickedPos.getX();

        if (component.getIndicator() == AffectedAreaIndicator.SINGLE) {
            return Set.of(clickedPos);

        } else if (component.getIndicator() == AffectedAreaIndicator.LINE) {
            return calculateAffectedPosEntitiesForLine(sourcePos, clickedPos, maxImpact, minImpact, yPos, xPos);

        } else if (component.getIndicator() == AffectedAreaIndicator.DIAMON_SELFCAST && sourcePos.equals(clickedPos)) {
            result.addAll(getBaseSquare(sourcePos, maxImpact));
            result.removeIf(pos -> Math.abs(pos.getX() - sourcePos.getX()) + Math.abs(pos.getY() - sourcePos.getY()) > maxImpact);
            result.removeIf(pos -> Math.abs(pos.getX() - sourcePos.getX()) <= minImpact || Math.abs(pos.getY() - sourcePos.getY()) <= minImpact);

        } else {
            result.addAll(getBaseSquare(clickedPos, maxImpact));

            if (component.getIndicator() == AffectedAreaIndicator.PLUS) {
                result.removeIf(pos -> !(pos.getX() == xPos || pos.getY() == yPos));
                result.removeAll(getBaseSquare(clickedPos, minImpact));

            } else if (component.getIndicator() == AffectedAreaIndicator.DIAMOND) {
                result.removeIf(pos -> Math.abs(pos.getX() - xPos) + Math.abs(pos.getY() - yPos) > maxImpact
                        || Math.abs(pos.getX() - xPos) + Math.abs(pos.getY() - yPos) <= minImpact);
            }
        }
        return result;
    }

    private static List<PositionComponent> getBaseSquare(PositionComponent pos, int impact) {
        // add big square
        List<PositionComponent> resultTmp = new ArrayList<>();
        if (impact <= 0) {
            return resultTmp;
        }
        for (int y = pos.getY() - impact; y <= pos.getY() + impact; y++) {
            for (int x = pos.getX() - impact; x <= pos.getX() + impact; x++) {
                resultTmp.add(new PositionComponent(x, y));
            }
        }
        return resultTmp;
    }

    private static Set<PositionComponent> calculateAffectedPosEntitiesForLine(PositionComponent sourcePos, PositionComponent clickedPos, int maxImpact, int minImpact, int yPos, int xPos) {
        Set<PositionComponent> result = new HashSet<>();
        int signumY = (int) Math.signum(clickedPos.getY() - sourcePos.getY());
        int impact = maxImpact - minImpact;
        Function<Integer, Boolean> testY = signumY < 0
                ? y -> y > yPos - impact
                : y -> y < yPos + impact;
        int signumX = (int) Math.signum(clickedPos.getX() - sourcePos.getX());
        Function<Integer, Boolean> testX = signumX < 0
                ? x -> x > xPos - impact
                : x -> x < xPos + impact;

        if (Math.abs(sourcePos.getX() - clickedPos.getX()) < Math.abs(sourcePos.getY() - clickedPos.getY())) {
            int signum = (int) Math.signum(clickedPos.getY() - sourcePos.getY());

            for (int y = yPos; testY.apply(y); y += signum) {
                result.add(new PositionComponent(xPos, y));
            }

        } else if (Math.abs(sourcePos.getX() - clickedPos.getX()) > Math.abs(sourcePos.getY() - clickedPos.getY())) {
            // from left or right
            for (int x = xPos; testX.apply(x); x += signumX) {
                result.add(new PositionComponent(x, yPos));
            }

        } else if (signumY != 0 && signumX != 0) {
            // diagonal
            for (int x = xPos, y = yPos; testX.apply(x) && testY.apply(y); x += signumX, y += signumY) {
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

    // TODO: 07.03.2021 refactor
    public static PositionComponent getDisplacementGoal(EntityData entityData, PositionComponent posEntityToDisplace, PositionComponent posSource, int entity, int displacement) {
        if (posEntityToDisplace.equals(posSource)) {
            return posSource;
        } else if (Math.abs(posEntityToDisplace.getX() - posSource.getX()) < Math.abs(posEntityToDisplace.getY() - posSource.getY())) {
            // displacement from top or bot
            int displacementSignum = (int) Math.signum(posEntityToDisplace.getY() - posSource.getY());

            Function<Integer, Boolean> predicate = number -> posSource.getY() < posEntityToDisplace.getY()
                    ? number < displacement
                    : number > -displacement;

            PositionComponent posNew = posEntityToDisplace;
            for (int y = 0; predicate.apply(y); y += displacementSignum) {
                boolean positionIsFree = isPositionIsFree(entityData, new PositionComponent(posEntityToDisplace.getX(), posEntityToDisplace.getY() + y), entity);
                if (positionIsFree) {
                    posNew = new PositionComponent(posEntityToDisplace.getX(), posEntityToDisplace.getY() + y);
                } else {
                    return posNew;
                }
            }
            return posNew;
        } else {
            // displacement from right or left
            int displacementSignum = (int) Math.signum(posEntityToDisplace.getX() - posSource.getX());
            Function<Integer, Boolean> predicate = number -> posSource.getX() < posEntityToDisplace.getX()
                    ? number < displacement
                    : number > -displacement;

            PositionComponent posNew = posEntityToDisplace;
            for (int x = 0; predicate.apply(x); x += displacementSignum) {
                boolean positionIsFree = isPositionIsFree(entityData, new PositionComponent(posEntityToDisplace.getX() + x, posEntityToDisplace.getY()), entity);
                if (positionIsFree) {
                    posNew = new PositionComponent(posEntityToDisplace.getX() + x, posEntityToDisplace.getY());
                } else {
                    return posNew;
                }
            }
            return posNew;
        }
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

}
