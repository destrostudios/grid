package com.destrostudios.grid.eventbus.handler;

import com.destrostudios.grid.components.properties.AttackPointsComponent;
import com.destrostudios.grid.components.properties.MovementPointsComponent;
import com.destrostudios.grid.components.spells.*;
import com.destrostudios.grid.components.spells.buffs.AttackPointsBuffComponent;
import com.destrostudios.grid.components.spells.buffs.HealthPointBuffComponent;
import com.destrostudios.grid.components.spells.buffs.MovementPointBuffComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.Eventbus;
import com.destrostudios.grid.eventbus.events.*;
import com.destrostudios.grid.eventbus.events.properties.AttackPointsChangedEvent;
import com.destrostudios.grid.eventbus.events.properties.MovementPointsChangedEvent;
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

        int spell = event.getSpell();
        Optional<CooldownComponent> cooldownComponent = entityWorld.getComponent(spell, CooldownComponent.class);
        cooldownComponent.ifPresent(component -> entityWorld.addComponent(spell, new OnCooldownComponent(component.getCooldown())));

        int playerEntity = event.getPlayerEntity();
        Optional<DamageComponent> damageSpell = entityWorld.getComponent(spell, DamageComponent.class);

        List<Event> followUpEvents = new ArrayList<>();
        Optional<AttackPointCostComponent> apCost = entityWorld.getComponent(event.getSpell(), AttackPointCostComponent.class);

        if (apCost.isPresent()) {
            AttackPointCostComponent attackPointCostComponent = apCost.get();
            AttackPointsComponent ap = entityWorld.getComponent(event.getPlayerEntity(), AttackPointsComponent.class).get();
            followUpEvents.add(new AttackPointsChangedEvent(event.getPlayerEntity(), ap.getAttackPoints() - attackPointCostComponent.getAttackPointCosts()));
        }

        Optional<MovementPointsCostComponent> mpCost = entityWorld.getComponent(event.getSpell(), MovementPointsCostComponent.class);
        if (mpCost.isPresent()) {
            MovementPointsCostComponent movementPointsCostComponent = mpCost.get();
            MovementPointsComponent mp = entityWorld.getComponent(event.getPlayerEntity(), MovementPointsComponent.class).get();
            followUpEvents.add(new MovementPointsChangedEvent(event.getPlayerEntity(), mp.getMovementPoints() - movementPointsCostComponent.getMovementPointsCost()));
        }

        damageSpell.ifPresent(damageComponent -> followUpEvents.add(new DamageTakenEvent(damageComponent.getDamage(), event.getTargetEntity())));

        if (entityWorld.hasComponents(spell, AttackPointsBuffComponent.class) || entityWorld.hasComponents(spell, MovementPointBuffComponent.class)
                || entityWorld.hasComponents(spell, HealthPointBuffComponent.class)) {

            followUpEvents.add(new BuffAddedEvent(playerEntity, spell));
        }
        eventbusInstance.registerSubEvents(followUpEvents);
    }
}
