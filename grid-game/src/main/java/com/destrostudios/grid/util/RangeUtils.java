package com.destrostudios.grid.util;

import com.destrostudios.grid.components.character.PlayerComponent;
import com.destrostudios.grid.components.map.ObstacleComponent;
import com.destrostudios.grid.components.map.PositionComponent;
import com.destrostudios.grid.components.map.WalkableComponent;
import com.destrostudios.grid.components.properties.BuffsComponent;
import com.destrostudios.grid.components.spells.buffs.BuffComponent;
import com.destrostudios.grid.components.spells.range.RangeComponent;
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

    public static int calculateTargetEntity(int x, int y, EntityData data) {
        Optional<Integer> targetEntity = data.list(PositionComponent.class).stream()
                .filter(e -> data.getComponent(e, PositionComponent.class).getX() == x
                        && data.getComponent(e, PositionComponent.class).getY() == y)
                .min((e1, e2) -> Boolean.compare(data.hasComponents(e2, PlayerComponent.class), data.hasComponents(e1, PlayerComponent.class)));

        return targetEntity.orElse(-1);
    }

    public static <E extends BuffComponent> int getBuff(int spellEntity, int playerEntity, EntityData data, Class<E> classz) {
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

}
