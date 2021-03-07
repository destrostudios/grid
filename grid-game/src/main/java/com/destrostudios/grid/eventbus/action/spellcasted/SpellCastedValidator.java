package com.destrostudios.grid.eventbus.action.spellcasted;

import com.destrostudios.grid.components.character.TurnComponent;
import com.destrostudios.grid.components.map.PositionComponent;
import com.destrostudios.grid.components.properties.AttackPointsComponent;
import com.destrostudios.grid.components.properties.HealthPointsComponent;
import com.destrostudios.grid.components.properties.MovementPointsComponent;
import com.destrostudios.grid.components.spells.limitations.CostComponent;
import com.destrostudios.grid.components.spells.limitations.OnCooldownComponent;
import com.destrostudios.grid.components.spells.movements.TeleportComponent;
import com.destrostudios.grid.components.spells.perturn.CastsPerTurnComponent;
import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.eventbus.EventValidator;
import com.destrostudios.grid.util.RangeUtils;
import java.util.function.Supplier;

import static com.destrostudios.grid.util.RangeUtils.isPositionIsFree;

public class SpellCastedValidator implements EventValidator<SpellCastedEvent> {
    @Override
    public boolean validate(SpellCastedEvent event, Supplier<EntityData> entityDataSupplier) {
        EntityData entityData = entityDataSupplier.get();

        if (!entityData.hasComponents(event.getPlayerEntity(), TurnComponent.class)) {
            return false;
        }

        // check Range
        int target = RangeUtils.calculateTargetEntity(event.getX(), event.getY(), entityData);
        PositionComponent position = entityData.getComponent(target, PositionComponent.class);
        AttackPointsComponent attackPointsPlayer = entityData.getComponent(event.getPlayerEntity(), AttackPointsComponent.class);
        MovementPointsComponent movementPointsPlayer = entityData.getComponent(event.getPlayerEntity(), MovementPointsComponent.class);
        HealthPointsComponent healthPointsComponent = entityData.getComponent(event.getPlayerEntity(), HealthPointsComponent.class);
        CostComponent costComponent = entityData.getComponent(event.getSpell(), CostComponent.class);
        CastsPerTurnComponent castsPerTurnComponent = entityData.getComponent(event.getSpell(), CastsPerTurnComponent.class);

        boolean fieldIsReachable = RangeUtils.getRangePosComponents(event.getSpell(), event.getPlayerEntity(), entityData).contains(position);
        boolean isOnCooldown = entityData.hasComponents(event.getSpell(), OnCooldownComponent.class);
        boolean teleportCanBeDone = !entityData.hasComponents(event.getSpell(), TeleportComponent.class) || isPositionIsFree(entityData, position, event.getPlayerEntity())
                && entityData.hasComponents(event.getSpell(), TeleportComponent.class);
        boolean costsCanBePayed = attackPointsPlayer.getAttackPoints() >= costComponent.getApCost()
                && movementPointsPlayer.getMovementPoints() >= costComponent.getMpCost()
                && healthPointsComponent.getHealth() >= costComponent.getHpCost();
        boolean maxCastsReaced = castsPerTurnComponent != null && castsPerTurnComponent.getMaxCastsPerTurn() == castsPerTurnComponent.getCastsThisTurn();

        return fieldIsReachable && !isOnCooldown && teleportCanBeDone && costsCanBePayed && !maxCastsReaced;
    }
}
