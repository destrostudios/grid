package com.destrostudios.grid.eventbus.validator;

import com.destrostudios.grid.components.character.RoundComponent;
import com.destrostudios.grid.components.map.PositionComponent;
import com.destrostudios.grid.components.properties.AttackPointsComponent;
import com.destrostudios.grid.components.properties.MovementPointsComponent;
import com.destrostudios.grid.components.spells.AttackPointCostComponent;
import com.destrostudios.grid.components.spells.MovementPointsCostComponent;
import com.destrostudios.grid.components.spells.OnCooldownComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.events.SpellCastedEvent;
import com.destrostudios.grid.util.CalculationUtils;

import java.util.List;
import java.util.function.Supplier;

public class SpellCastedValidator implements EventValidator<SpellCastedEvent> {
    @Override
    public boolean validate(SpellCastedEvent event, Supplier<EntityWorld> entityWorldSupplier) {
        EntityWorld entityWorld = entityWorldSupplier.get();

        if (!entityWorld.hasComponents(event.getPlayerEntity(), RoundComponent.class)) {
            return false;
        }

        // check Range
        PositionComponent position = entityWorld.getComponent(event.getTargetEntity(), PositionComponent.class);
        List<PositionComponent> rangeEntites = CalculationUtils.getRangePosComponents(event.getSpell(), event.getPlayerEntity(), entityWorld);

        boolean fieldIsReachable = rangeEntites.contains(position);
        boolean isOnCooldown = entityWorld.hasComponents(event.getSpell(), OnCooldownComponent.class);

        if (fieldIsReachable && !isOnCooldown) {
            // check AP costs
            AttackPointsComponent attackPointsPlayer = entityWorld.getComponent(event.getPlayerEntity(), AttackPointsComponent.class);

            if (entityWorld.hasComponents(event.getSpell(), AttackPointCostComponent.class)
                    && entityWorld.getComponent(event.getSpell(), AttackPointCostComponent.class).getAttackPointCosts() > attackPointsPlayer.getAttackPoints()) {
                return false;
            }

            // check MP costs
            MovementPointsCostComponent mpCostsSpell = entityWorld.getComponent(event.getSpell(), MovementPointsCostComponent.class);
            MovementPointsComponent movementPointsPlayer = entityWorld.getComponent(event.getPlayerEntity(), MovementPointsComponent.class);

            return mpCostsSpell == null || mpCostsSpell.getMovementPointsCost() <= movementPointsPlayer.getMovementPoints();
        }
        return false;
    }
}
