package com.destrostudios.grid.eventbus.action.spellcasted;

import com.destrostudios.grid.components.character.ActiveTurnComponent;
import com.destrostudios.grid.components.character.PlayerComponent;
import com.destrostudios.grid.components.map.PositionComponent;
import com.destrostudios.grid.components.properties.AttackPointsComponent;
import com.destrostudios.grid.components.properties.HealthPointsComponent;
import com.destrostudios.grid.components.properties.MovementPointsComponent;
import com.destrostudios.grid.components.spells.limitations.CostComponent;
import com.destrostudios.grid.components.spells.limitations.OnCooldownComponent;
import com.destrostudios.grid.components.spells.limitations.RequiresTargetComponent;
import com.destrostudios.grid.components.spells.movements.TeleportComponent;
import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.eventbus.EventValidator;
import com.destrostudios.grid.util.SpellUtils;

import java.util.function.Supplier;

import static com.destrostudios.grid.util.RangeUtils.getRangePosComponents;
import static com.destrostudios.grid.util.SpellUtils.isPositionIsFree;

public class SpellCastedValidator implements EventValidator<SpellCastedEvent> {
    @Override
    public boolean validate(SpellCastedEvent event, Supplier<EntityData> entityDataSupplier) {
        EntityData entityData = entityDataSupplier.get();

        if (!entityData.hasComponents(event.getPlayerEntity(), ActiveTurnComponent.class)) {
            return false;
        }

        // check Range
        int target = SpellUtils.calculateTargetEntity(event.getX(), event.getY(), entityData);
        PositionComponent position = entityData.getComponent(target, PositionComponent.class);
        AttackPointsComponent attackPointsPlayer = entityData.getComponent(event.getPlayerEntity(), AttackPointsComponent.class);
        MovementPointsComponent movementPointsPlayer = entityData.getComponent(event.getPlayerEntity(), MovementPointsComponent.class);
        HealthPointsComponent healthPointsComponent = entityData.getComponent(event.getPlayerEntity(), HealthPointsComponent.class);
        CostComponent costComponent = entityData.getComponent(event.getSpell(), CostComponent.class);

        boolean fieldIsReachable = getRangePosComponents(event.getSpell(), event.getPlayerEntity(), entityData).contains(position);
        boolean isOnCooldown = entityData.hasComponents(event.getSpell(), OnCooldownComponent.class);
        boolean teleportCanBeDone = !entityData.hasComponents(event.getSpell(), TeleportComponent.class) || isPositionIsFree(entityData, position, event.getPlayerEntity())
                && entityData.hasComponents(event.getSpell(), TeleportComponent.class);
        boolean costsCanBePayed = attackPointsPlayer.getAttackPoints() >= costComponent.getApCost()
                && movementPointsPlayer.getMovementPoints() >= costComponent.getMpCost()
                && healthPointsComponent.getHealth() >= costComponent.getHpCost();
        boolean requiresTarget = !entityData.hasComponents(event.getSpell(), RequiresTargetComponent.class) || entityData.hasComponents(target, PlayerComponent.class);
        return requiresTarget && fieldIsReachable && !isOnCooldown && teleportCanBeDone && costsCanBePayed && SpellUtils.maxCastsNotReached(event.getSpell(), entityData);
    }
}
