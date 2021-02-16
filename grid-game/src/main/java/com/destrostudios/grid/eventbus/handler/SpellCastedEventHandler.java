package com.destrostudios.grid.eventbus.handler;

import com.destrostudios.grid.components.properties.MovementPointsComponent;
import com.destrostudios.grid.components.spells.*;
import com.destrostudios.grid.components.spells.buffs.AttackPointsBuffComponent;
import com.destrostudios.grid.components.spells.buffs.HealthPointBuffComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.Eventbus;
import com.destrostudios.grid.eventbus.events.DamageTakenEvent;
import com.destrostudios.grid.eventbus.events.Event;
import com.destrostudios.grid.eventbus.events.BuffAddedEvent;
import com.destrostudios.grid.eventbus.events.SpellCastedEvent;
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

        if (cooldownComponent.isPresent()) {
            entityWorld.addComponent(spell, new OnCooldownComponent(cooldownComponent.get().getCooldown()));
        }
        int playerEntity = event.getPlayerEntity();
        Optional<DamageComponent> damageSpell = entityWorld.getComponent(spell, DamageComponent.class);

        List<Event> followUpEvents = new ArrayList<>();
        damageSpell.ifPresent(damageComponent -> followUpEvents.add(new DamageTakenEvent(damageComponent.getDamage(), event.getTargetEntity())));

        if (entityWorld.hasComponents(spell, AttackPointsBuffComponent.class) || entityWorld.hasComponents(spell, MovementPointsComponent.class)
                || entityWorld.hasComponents(spell, HealthPointBuffComponent.class)) {

            followUpEvents.add(new BuffAddedEvent(playerEntity, spell));
        }
        eventbusInstance.registerSubEvents(followUpEvents);
    }
}
