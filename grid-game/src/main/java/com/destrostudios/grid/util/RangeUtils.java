package com.destrostudios.grid.util;

import com.destrostudios.grid.components.character.PlayerComponent;
import com.destrostudios.grid.components.map.ObstacleComponent;
import com.destrostudios.grid.components.map.PositionComponent;
import com.destrostudios.grid.components.map.WalkableComponent;
import com.destrostudios.grid.components.properties.BuffsComponent;
import com.destrostudios.grid.components.spells.buffs.BuffComponent;
import com.destrostudios.grid.components.spells.range.AffectedAreaComponent;
import com.destrostudios.grid.components.spells.range.AffectedAreaIndicator;
import com.destrostudios.grid.components.spells.range.RangeComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.google.common.collect.Lists;

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
     * @param entityWorld  with entites
     * @return empty List, if it is a self target spell and List of entities of targetable spells otherwise
     */
    public static List<Integer> getRange(int spellEntity, int casterEntity, EntityWorld entityWorld) {
        RangeComponent rangeComponentOpt = entityWorld.getComponent(spellEntity, RangeComponent.class);
        PositionComponent casterPositionOpt = entityWorld.getComponent(casterEntity, PositionComponent.class);
        int maxRange = rangeComponentOpt.getMaxRange();
        int minRange = rangeComponentOpt.getMinRange();
        PositionComponent positionComponent = casterPositionOpt;
        int x = positionComponent.getX();
        int y = positionComponent.getY();
        List<Integer> walkableAndTargetablePos = entityWorld.list(PositionComponent.class, WalkableComponent.class);
        List<Integer> result = new ArrayList<>();

        for (int walkableAndTargetablePo : walkableAndTargetablePos) {
            PositionComponent posC = entityWorld.getComponent(walkableAndTargetablePo, PositionComponent.class);
            if (Math.abs(posC.getX() - x) + Math.abs(posC.getY() - y) <= maxRange
                    && Math.abs(posC.getX() - x) + Math.abs(posC.getY() - y) >= minRange) {
                result.add(walkableAndTargetablePo);
            }
        }
        return result;
    }

    public static List<Integer> getAffectedEntities(int spellEntity, PositionComponent sourcePos, PositionComponent clickedPos, EntityWorld world) {
        List<PositionComponent> positionComponents = calculateAffectedPosEntities(spellEntity, sourcePos, clickedPos, world);
        return world.list(PlayerComponent.class).stream()
                .filter(e -> positionComponents.contains(world.getComponent(e, PositionComponent.class)))
                .collect(Collectors.toList());
    }

    public static List<PositionComponent> calculateAffectedPosEntities(int spellEntity, PositionComponent sourcePos, PositionComponent clickedPos, EntityWorld world) {
        AffectedAreaComponent component = world.getComponent(spellEntity, AffectedAreaComponent.class);
        int impact = component.getImpact();
        int halfImpact = impact / 2;
        int yPos = sourcePos.getY();
        int xPos = sourcePos.getX();

        List<PositionComponent> result = Lists.newArrayList(clickedPos);

        if (component.getIndicator() == AffectedAreaIndicator.LINE) {
            if (sourcePos.getX() == clickedPos.getX()) {
                // from bot or top
                int signum = (int) Math.signum(clickedPos.getY() - sourcePos.getY());
                for (int y = yPos; y < signum * impact; y += signum) {
                    result.add(new PositionComponent(xPos, y));
                }

            } else if (sourcePos.getY() == clickedPos.getY()) {
                // from left or right
                int signum = (int) Math.signum(clickedPos.getX() - sourcePos.getX());
                for (int x = xPos; x < signum * impact; x += signum) {
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
            for (int y = yPos - halfImpact; y < yPos + halfImpact; y++) {
                // TODO: 06.03.21
            }
        }
        return result;
    }

    public static int calculateTargetEntity(int x, int y, EntityWorld world) {
        Optional<Integer> targetEntity = world.list(PositionComponent.class).stream()
                .filter(e -> world.getComponent(e, PositionComponent.class).getX() == x
                        && world.getComponent(e, PositionComponent.class).getY() == y)
                .min((e1, e2) -> Boolean.compare(world.hasComponents(e2, PlayerComponent.class), world.hasComponents(e1, PlayerComponent.class)));

        return targetEntity.orElse(-1);
    }

    public static <E extends BuffComponent> int getBuffAmount(int spellEntity, int playerEntity, EntityWorld world, Class<E> classz) {
        int buffPlayer = world.hasComponents(playerEntity, classz)
                ? world.getComponent(playerEntity, classz).getBuffAmount()
                : 0;
        List<Integer> spellBuffEntities = world.hasComponents(spellEntity, BuffsComponent.class)
                ? world.getComponent(spellEntity, BuffsComponent.class).getBuffEntities()
                : new ArrayList<>();
        int buffSpell = spellBuffEntities.stream()
                .flatMap(spellBuff -> world.getComponents(spellBuff).stream())
                .filter(classz::isInstance)
                .map(spellBuff -> (BuffComponent) spellBuff)
                .mapToInt(BuffComponent::getBuffAmount)
                .sum();
        return buffPlayer + buffSpell;
    }

    public static List<PositionComponent> getRangePosComponents(int spellEntity, int casterEntity, EntityWorld entityWorld) {
        return getRange(spellEntity, casterEntity, entityWorld).stream()
                .map(e -> entityWorld.getComponent(e, PositionComponent.class))
                .collect(Collectors.toList());
    }

    public static boolean isPositionIsFree(EntityWorld entityWorld, PositionComponent newPosition, int entity) {
        List<Integer> allPlayersEntites = entityWorld.list(PositionComponent.class, PlayerComponent.class).stream()
                .filter(e -> e != entity)
                .collect(Collectors.toList());
        List<Integer> allObstacleEntites = entityWorld.list(PositionComponent.class, ObstacleComponent.class).stream()
                .filter(e -> e != entity)
                .collect(Collectors.toList());
        List<Integer> allWalkableEntities = entityWorld.list(PositionComponent.class, WalkableComponent.class).stream()
                .filter(e -> e != entity)
                .collect(Collectors.toList());

        boolean collidesWithOtherPlayer = allPlayersEntites.stream().anyMatch(pE -> newPosition.equals(entityWorld.getComponent(pE, PositionComponent.class)));
        boolean collidesWithObstacle = allObstacleEntites.stream().anyMatch(pE -> newPosition.equals(entityWorld.getComponent(pE, PositionComponent.class)));
        boolean isWalkableField = allWalkableEntities.stream().anyMatch(pE -> newPosition.equals(entityWorld.getComponent(pE, PositionComponent.class)));

        return isWalkableField && !collidesWithOtherPlayer && !collidesWithObstacle;
    }

    public static PositionComponent getDisplacementGoal(EntityWorld entityWorld, PositionComponent posEntityToDisplace, PositionComponent posSource, int entity, int displacement) {
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
                boolean positionIsFree = isPositionIsFree(entityWorld, new PositionComponent(posEntityToDisplace.getX(), posEntityToDisplace.getY() + y), entity);
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
                boolean positionIsFree = isPositionIsFree(entityWorld, new PositionComponent(posEntityToDisplace.getX() + x, posEntityToDisplace.getY()), entity);
                if (positionIsFree) {
                    posNew = new PositionComponent(posEntityToDisplace.getX() + x, posEntityToDisplace.getY());
                } else {
                    return posNew;
                }
            }
            return posNew;
        }
    }

}
