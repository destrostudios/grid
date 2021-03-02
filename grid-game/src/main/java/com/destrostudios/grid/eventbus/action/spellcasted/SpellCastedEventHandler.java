package com.destrostudios.grid.eventbus.action.spellcasted;

import com.destrostudios.grid.components.character.PlayerComponent;
import com.destrostudios.grid.components.map.PositionComponent;
import com.destrostudios.grid.components.properties.AttackPointsComponent;
import com.destrostudios.grid.components.properties.MovementPointsComponent;
import com.destrostudios.grid.components.spells.*;
import com.destrostudios.grid.components.spells.buffs.AttackPointsBuffComponent;
import com.destrostudios.grid.components.spells.buffs.HealthPointBuffComponent;
import com.destrostudios.grid.components.spells.buffs.MovementPointBuffComponent;
import com.destrostudios.grid.components.spells.poison.AttackPointsPoisonComponent;
import com.destrostudios.grid.components.spells.poison.HealthPointsPoisonComponent;
import com.destrostudios.grid.components.spells.poison.MovementPointsPoisonComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.Event;
import com.destrostudios.grid.eventbus.EventHandler;
import com.destrostudios.grid.eventbus.Eventbus;
import com.destrostudios.grid.eventbus.action.damagetaken.DamageTakenEvent;
import com.destrostudios.grid.eventbus.action.teleport.TeleportEvent;
import com.destrostudios.grid.eventbus.add.buff.BuffAddedEvent;
import com.destrostudios.grid.eventbus.add.poison.PoisonAddedEvent;
import com.destrostudios.grid.eventbus.update.ap.AttackPointsChangedEvent;
import com.destrostudios.grid.eventbus.update.mp.MovementPointsChangedEvent;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static com.destrostudios.grid.util.CalculationUtils.calculateTargetEntity;

@AllArgsConstructor
public class SpellCastedEventHandler implements EventHandler<SpellCastedEvent> {
    private final Eventbus eventbusInstance;

    @Override
    public void onEvent(SpellCastedEvent event, Supplier<EntityWorld> entityWorldSupplier) {
        EntityWorld entityWorld = entityWorldSupplier.get();
        int spell = event.getSpell();
        int targetEntity = calculateTargetEntity(event.getX(), event.getY(), entityWorld);
        if (entityWorld.hasComponents(spell, CooldownComponent.class)) {
            entityWorld.addComponent(spell, new OnCooldownComponent(entityWorld.getComponent(spell, CooldownComponent.class).getCooldown()));
        }

        int playerEntity = event.getPlayerEntity();
        List<Event> followUpEvents = new ArrayList<>();

        if (entityWorld.hasComponents(event.getSpell(), AttackPointCostComponent.class)) {
            AttackPointCostComponent attackPointCostComponent = entityWorld.getComponent(event.getSpell(), AttackPointCostComponent.class);
            AttackPointsComponent ap = entityWorld.getComponent(event.getPlayerEntity(), AttackPointsComponent.class);
            followUpEvents.add(new AttackPointsChangedEvent(event.getPlayerEntity(), ap.getAttackPoints() - attackPointCostComponent.getAttackPointCosts()));
        }

        if (entityWorld.hasComponents(event.getSpell(), MovementPointsCostComponent.class)) {
            MovementPointsCostComponent movementPointsCostComponent = entityWorld.getComponent(event.getSpell(), MovementPointsCostComponent.class);
            MovementPointsComponent mp = entityWorld.getComponent(event.getPlayerEntity(), MovementPointsComponent.class);
            followUpEvents.add(new MovementPointsChangedEvent(event.getPlayerEntity(), mp.getMovementPoints() - movementPointsCostComponent.getMovementPointsCost()));
        }

        if (entityWorld.hasComponents(spell, DamageComponent.class)) {
            followUpEvents.add(new DamageTakenEvent(entityWorld.getComponent(spell, DamageComponent.class).getDamage(), targetEntity));
        }

        if (entityWorld.hasComponents(spell, AttackPointsBuffComponent.class) || entityWorld.hasComponents(spell, MovementPointBuffComponent.class)
                || entityWorld.hasComponents(spell, HealthPointBuffComponent.class)) {

            followUpEvents.add(new BuffAddedEvent(playerEntity, spell));
        }

        if (entityWorld.hasComponents(spell, AttackPointsPoisonComponent.class) || entityWorld.hasComponents(spell, MovementPointsPoisonComponent.class)
                || entityWorld.hasComponents(spell, HealthPointsPoisonComponent.class)) {
            followUpEvents.add(new PoisonAddedEvent(playerEntity, targetEntity, spell));
        }
        if (entityWorld.hasComponents(spell, TeleportComponent.class)) {
            followUpEvents.add(new TeleportEvent(playerEntity, event.getX(), event.getY()));
        }
        eventbusInstance.registerSubEvents(followUpEvents);
    }


}
