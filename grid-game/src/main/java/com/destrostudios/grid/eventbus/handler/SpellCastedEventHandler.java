package com.destrostudios.grid.eventbus.handler;

import com.destrostudios.grid.components.properties.AttackPointsComponent;
import com.destrostudios.grid.components.properties.HealthPointsComponent;
import com.destrostudios.grid.components.properties.MaxHealthComponent;
import com.destrostudios.grid.components.properties.MovementPointsComponent;
import com.destrostudios.grid.components.spells.AttackPointCostComponent;
import com.destrostudios.grid.components.spells.DamageComponent;
import com.destrostudios.grid.components.spells.MovementPointsCostComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.Eventbus;
import com.destrostudios.grid.eventbus.events.*;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@AllArgsConstructor
public class SpellCastedEventHandler implements EventHandler<SpellCastedEvent> {
    private final Eventbus eventbusInstance;

    @Override
    public void onEvent(SpellCastedEvent event, Supplier<EntityWorld> entityWorldSupplier) {
        EntityWorld entityWorld = entityWorldSupplier.get();

        Optional<AttackPointCostComponent> apCostsSpell = entityWorld.getComponent(event.getSpell(), AttackPointCostComponent.class);
        Optional<DamageComponent> damageSpell = entityWorld.getComponent(event.getSpell(), DamageComponent.class);
        Optional<AttackPointsComponent> attackPointsPlayer = entityWorld.getComponent(event.getPlayerEntity(), AttackPointsComponent.class);
        Optional<MovementPointsComponent> movementPointsPlayer = entityWorld.getComponent(event.getPlayerEntity(), MovementPointsComponent.class);
        Optional<MovementPointsCostComponent> mpCostSpell = entityWorld.getComponent(event.getSpell(), MovementPointsCostComponent.class);
        Optional<MovementPointsComponent> mpBuffSpell = entityWorld.getComponent(event.getSpell(), MovementPointsComponent.class);
        Optional<AttackPointsComponent> apBuffSpell = entityWorld.getComponent(event.getSpell(), AttackPointsComponent.class);
        Optional<HealthPointsComponent> hpBuffSpell = entityWorld.getComponent(event.getSpell(), HealthPointsComponent.class);

        int ap = attackPointsPlayer.get().getAttackPoints();

        // followup events
        List<Event> followUpEvents = new ArrayList<>();
        if (apCostsSpell.isPresent()) {
            // ap costs
            int newAttackPoints = ap - apCostsSpell.get().getAttackPointCosts();
            followUpEvents.add(new AttackPointsChangedEvent(event.getPlayerEntity(), newAttackPoints));
        }

        if (entityWorld.hasComponents(event.getSpell(), MovementPointsCostComponent.class)) {
            // mp costs
            Optional<MovementPointsComponent> mp = entityWorld.getComponent(event.getPlayerEntity(), MovementPointsComponent.class);
            int newMp = mp.get().getMovementPoints() - mpCostSpell.get().getMovementPointsCost();
            followUpEvents.add(new MovementPointsChangedEvent(event.getPlayerEntity(), newMp));
        }

        damageSpell.ifPresent(damageComponent -> followUpEvents.add(new DamageTakenEvent(damageComponent.getDamage(), event.getTargetEntity())));

        if (mpBuffSpell.isPresent()) {
            // mp buff
            int newMp = mpBuffSpell.get().getMovementPoints() + movementPointsPlayer.get().getMovementPoints();
            followUpEvents.add(new MovementPointsChangedEvent(event.getPlayerEntity(), newMp));
        }

        if (apBuffSpell.isPresent()) {
            // ap buff
            int newAp = apBuffSpell.get().getAttackPoints() + attackPointsPlayer.get().getAttackPoints();
            followUpEvents.add(new AttackPointsChangedEvent(event.getPlayerEntity(), newAp));
        }
        if (hpBuffSpell.isPresent()) {
            // hp buff
            Optional<HealthPointsComponent> hpPlayer = entityWorld.getComponent(event.getPlayerEntity(), HealthPointsComponent.class);
            Optional<MaxHealthComponent> maxHp = entityWorld.getComponent(event.getPlayerEntity(), MaxHealthComponent.class);

            int newMaxHp = maxHp.get().getMaxHealth() + hpBuffSpell.get().getHealth();
            followUpEvents.add(new MaxHealPointsChangedEvent(event.getPlayerEntity(), newMaxHp));
            int newHp = hpPlayer.get().getHealth() + hpBuffSpell.get().getHealth();
            followUpEvents.add(new HealthPointsChangedEvent(event.getPlayerEntity(), newHp));
        }
        eventbusInstance.registerSubEvents(followUpEvents);
    }
}
