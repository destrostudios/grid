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
import com.destrostudios.grid.entities.EntityWorld;
import com.google.common.collect.Lists;

import com.destrostudios.grid.entities.EntityData;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RangeUtils {
    /**
     * Calculates the entities of the targetable spells.
     *
     * @param spellEntity  with range
     * @param casterEntity of the spell caster
     * @param entityData   with entites
     * @return empty List, if it is a self target spell and List of entities of targetable spells otherwise
     */
    public static List<Integer> getRange(int spellEntity, int casterEntity, EntityData entityData) {
        RangeComponent rangeComponentOpt = entityData.getComponent(spellEntity, RangeComponent.class);
        PositionComponent casterPositionOpt = entityData.getComponent(casterEntity, PositionComponent.class);
        int maxRange = rangeComponentOpt.getMaxRange();
        int minRange = rangeComponentOpt.getMinRange();
        PositionComponent positionComponent = casterPositionOpt;
        int x = positionComponent.getX();
        int y = positionComponent.getY();
        List<Integer> walkableAndTargetablePos = entityData.list(PositionComponent.class, WalkableComponent.class);
        List<Integer> result = new ArrayList<>();

        for (int walkableAndTargetablePo : walkableAndTargetablePos) {
            PositionComponent posC = entityData.getComponent(walkableAndTargetablePo, PositionComponent.class);
            if (Math.abs(posC.getX() - x) + Math.abs(posC.getY() - y) <= maxRange
                    && Math.abs(posC.getX() - x) + Math.abs(posC.getY() - y) >= minRange) {
                result.add(walkableAndTargetablePo);
            }
        }
        return result;
    }

    public static List<Integer> getAffectedEntities(int spellEntity, PositionComponent sourcePos, PositionComponent clickedPos, EntityData entityData) {
        List<PositionComponent> positionComponents = calculateAffectedPosEntities(spellEntity, sourcePos, clickedPos, entityData);
        return entityData.list(PlayerComponent.class).stream()
                .filter(e -> positionComponents.contains(entityData.getComponent(e, PositionComponent.class)))
                .collect(Collectors.toList());
    }

    public static List<PositionComponent> calculateAffectedPosEntities(int spellEntity, PositionComponent sourcePos, PositionComponent clickedPos, EntityData entityData) {
        AffectedAreaComponent component = entityData.getComponent(spellEntity, AffectedAreaComponent.class);
        int impact = component.getImpact();
        int halfImpact = impact / 2;
        int yPos = clickedPos.getY();
        int xPos = clickedPos.getX();

        List<PositionComponent> result = Lists.newArrayList();

        if (component.getIndicator() == AffectedAreaIndicator.LINE) {
            if (sourcePos.getX() == clickedPos.getX()) {
                // from bot or top
                int signum = (int) Math.signum(clickedPos.getY() - sourcePos.getY());
                Function<Integer, Boolean> test = signum < 0
                        ? y -> y > yPos - impact
                        : y -> y < yPos + impact;
                for (int y = yPos; test.apply(y); y += signum) {
                    result.add(new PositionComponent(xPos, y));
                }

            } else if (sourcePos.getY() == clickedPos.getY()) {
                // from left or right
                int signum = (int) Math.signum(clickedPos.getX() - sourcePos.getX());
                Function<Integer, Boolean> test = signum < 0
                        ? x -> x > xPos - impact
                        : x -> x < xPos + impact;
                for (int x = xPos; test.apply(x); x += signum) {
                    result.add(new PositionComponent(x, yPos));
                }
            }

        } else if (component.getIndicator() == AffectedAreaIndicator.CROSS) {
            if (sourcePos.getX() == clickedPos.getX()) {
                // from bot or top
                for (int y = yPos - halfImpact; y <= yPos + halfImpact; y++) {
                    result.add(new PositionComponent(xPos, y));
                }

            } else if (sourcePos.getY() == clickedPos.getY()) {
                // from left or right
                for (int x = xPos - halfImpact; x <= xPos + halfImpact; x++) {
                    result.add(new PositionComponent(x, yPos));
                }
            }

        } else if (component.getIndicator() == AffectedAreaIndicator.CIRCLE) {
            // TODO: 07.03.2021 clarify, how it should look
            for (int y = yPos; y >= yPos - halfImpact; y--) {
                int delta = halfImpact;
                for (int x = xPos - halfImpact; x <= xPos + halfImpact && delta > 0; x++, delta = delta - 2) {
                    result.add(new PositionComponent(x, y));
                }
            }
            int delta = halfImpact - 2;
            for (int y = yPos + 1; y < yPos + halfImpact; y++) {
                for (int x = xPos - halfImpact; x < xPos + halfImpact && delta > 0; x++, delta = delta - 2) {
                    result.add(new PositionComponent(x, y));
                }
            }
        } else if (component.getIndicator() == AffectedAreaIndicator.SQUARE) {
            for (int y = yPos - halfImpact; y <= yPos + halfImpact; y++) {
                for (int x = xPos - halfImpact; x <= xPos + halfImpact; x++) {
                    result.add(new PositionComponent(x, y));
                }
            }
        } else {
            result.add(clickedPos);
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
        return getRange(spellEntity, casterEntity, entityData).stream()
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

    public boolean isCostPayable(int casterEntity, int spellEntity, EntityWorld entityWorld) {
        CostComponent cost = entityWorld.getComponent(spellEntity, CostComponent.class);
        AttackPointsComponent apComp = entityWorld.getComponent(casterEntity, AttackPointsComponent.class);
        MovementPointsComponent mpComp = entityWorld.getComponent(casterEntity, MovementPointsComponent.class);
        HealthPointsComponent hpComp = entityWorld.getComponent(casterEntity, HealthPointsComponent.class);
        return apComp.getAttackPoints() >= cost.getApCost() && mpComp.getMovementPoints() >= cost.getMpCost() && hpComp.getHealth() >= cost.getHpCost();
    }

}
