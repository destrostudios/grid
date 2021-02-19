package com.destrostudios.grid.util;

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

    public static List<PositionComponent> getRangePosComponents(int spellEntity, int casterEntity, EntityWorld entityWorld) {
        return getRange(spellEntity,casterEntity,entityWorld).stream()
                .map(e -> entityWorld.getComponent(e, PositionComponent.class))
                .collect(Collectors.toList());
    }
}
