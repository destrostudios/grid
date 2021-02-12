package com.destrostudios.grid.eventbus.handler;

import com.destrostudios.grid.components.AttackPointsComponent;
import com.destrostudios.grid.components.spells.AttackPointCostComponent;
import com.destrostudios.grid.components.spells.DamageComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.Eventbus;
import com.destrostudios.grid.eventbus.events.AttackPointsChangedEvent;
import com.destrostudios.grid.eventbus.events.DamageTakenEvent;
import com.destrostudios.grid.eventbus.events.SpellCastedEvent;
import lombok.AllArgsConstructor;

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

        int ap = attackPointsPlayer.get().getAttackPoints();
        int apCost = apCostsSpell.get().getAttackPointCosts();

        if (ap >= apCost) {
            // followup events
            AttackPointsChangedEvent attackPointsChangedEvent = new AttackPointsChangedEvent(event.getPlayerEntity(), ap - apCost);
            DamageTakenEvent damageTakenEvent = new DamageTakenEvent(damageSpell.get().getDamage(), event.getTargetEntity());
            eventbusInstance.registerSubEvents(attackPointsChangedEvent, damageTakenEvent);
        }
    }
}
