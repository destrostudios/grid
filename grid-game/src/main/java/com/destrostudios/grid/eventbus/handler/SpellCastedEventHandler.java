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
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@AllArgsConstructor
public class SpellCastedEventHandler implements EventHandler<SpellCastedEvent> {
    private final Eventbus eventbusInstance;

    @Override
    public void onEvent(SpellCastedEvent event, Supplier<EntityWorld> entityWorldSupplier) {
        EntityWorld entityWorld = entityWorldSupplier.get();
        int spell = event.getSpell();

        if (entityWorld.hasComponents(spell, CooldownComponent.class)) {
            entityWorld.addComponent(spell, new OnCooldownComponent( entityWorld.getComponent(spell, CooldownComponent.class).getCooldown()));
        }

        int playerEntity = event.getPlayerEntity();
        List<Event> followUpEvents = new ArrayList<>();

        if (entityWorld.hasComponents(event.getSpell(), AttackPointCostComponent.class)) {
            AttackPointCostComponent attackPointCostComponent = entityWorld.getComponent(event.getSpell(), AttackPointCostComponent.class);
            AttackPointsComponent ap = entityWorld.getComponent(event.getPlayerEntity(), AttackPointsComponent.class);
            followUpEvents.add(new PropertiePointsChangedEvent.AttackPointsChangedEvent(event.getPlayerEntity(), ap.getAttackPoints() - attackPointCostComponent.getAttackPointCosts()));
        }

        if (entityWorld.hasComponents(event.getSpell(), MovementPointsCostComponent.class)) {
            MovementPointsCostComponent movementPointsCostComponent = entityWorld.getComponent(event.getSpell(), MovementPointsCostComponent.class);
            MovementPointsComponent mp = entityWorld.getComponent(event.getPlayerEntity(), MovementPointsComponent.class);
            followUpEvents.add(new PropertiePointsChangedEvent.MovementPointsChangedEvent(event.getPlayerEntity(), mp.getMovementPoints() - movementPointsCostComponent.getMovementPointsCost()));
        }

        if (entityWorld.hasComponents(spell, DamageComponent.class)) {
            followUpEvents.add(new DamageTakenEvent(entityWorld.getComponent(spell, DamageComponent.class).getDamage(), event.getTargetEntity()));
        }

        if (entityWorld.hasComponents(spell, AttackPointsBuffComponent.class) || entityWorld.hasComponents(spell, MovementPointBuffComponent.class)
                || entityWorld.hasComponents(spell, HealthPointBuffComponent.class)) {

            followUpEvents.add(new BuffAddedEvent(playerEntity, spell));
        }
        eventbusInstance.registerSubEvents(followUpEvents);
    }
}
