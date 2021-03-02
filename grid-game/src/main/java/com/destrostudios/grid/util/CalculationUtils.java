package com.destrostudios.grid.util;

import com.destrostudios.grid.components.character.PlayerComponent;
import com.destrostudios.grid.components.map.ObstacleComponent;
import com.destrostudios.grid.components.map.PositionComponent;
import com.destrostudios.grid.components.map.WalkableComponent;
import com.destrostudios.grid.components.spells.RangeComponent;
import com.destrostudios.grid.entities.EntityWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CalculationUtils {
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
        int range = rangeComponentOpt.getRange();
        PositionComponent positionComponent = casterPositionOpt;
        int x = positionComponent.getX();
        int y = positionComponent.getY();
        List<Integer> walkableAndTargetablePos = entityWorld.list(PositionComponent.class, WalkableComponent.class);
        List<Integer> result = new ArrayList<>();
        for (int walkableAndTargetablePo : walkableAndTargetablePos) {
            PositionComponent posC = entityWorld.getComponent(walkableAndTargetablePo, PositionComponent.class);
            if (Math.abs(posC.getX() - x) + Math.abs(posC.getY() - y) <= range) {
                result.add(walkableAndTargetablePo);
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
}
