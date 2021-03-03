package com.destrostudios.grid.eventbus.action.spellcasted;

import com.destrostudios.grid.components.map.PositionComponent;
import com.destrostudios.grid.components.properties.AttackPointsComponent;
import com.destrostudios.grid.components.properties.MovementPointsComponent;
import com.destrostudios.grid.components.spells.*;
import com.destrostudios.grid.components.spells.buffs.*;
import com.destrostudios.grid.components.spells.poison.AttackPointsPerTurnComponent;
import com.destrostudios.grid.components.spells.poison.HealthPointsPerTurnComponent;
import com.destrostudios.grid.components.spells.poison.MovementPointsPerTurnComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.Event;
import com.destrostudios.grid.eventbus.EventHandler;
import com.destrostudios.grid.eventbus.Eventbus;
import com.destrostudios.grid.eventbus.action.damagetaken.DamageTakenEvent;
import com.destrostudios.grid.eventbus.action.displace.DisplacementEvent;
import com.destrostudios.grid.eventbus.action.healreceived.HealReceivedEvent;
import com.destrostudios.grid.eventbus.action.move.MoveEvent;
import com.destrostudios.grid.eventbus.action.move.MoveType;
import com.destrostudios.grid.eventbus.add.playerbuff.PlayerBuffAddedEvent;
import com.destrostudios.grid.eventbus.add.poison.PoisonAddedEvent;
import com.destrostudios.grid.eventbus.add.spellbuff.SpellBuffAddedEvent;
import com.destrostudios.grid.eventbus.update.ap.AttackPointsChangedEvent;
import com.destrostudios.grid.eventbus.update.mp.MovementPointsChangedEvent;
import com.destrostudios.grid.util.CalculationUtils;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
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

        if (entityWorld.hasComponents(spell, HealthChangeComponent.class)) {
            int healthChange = entityWorld.getComponent(spell, HealthChangeComponent.class).getHealthChange();
            Event hpEvent = healthChange < 0
                    ? new DamageTakenEvent(Math.abs(healthChange) + CalculationUtils.getBuff(spell, playerEntity, entityWorld, DamageBuffComponent.class), targetEntity)
                    : new HealReceivedEvent(healthChange + CalculationUtils.getBuff(spell, playerEntity, entityWorld, HealBuffComponent.class), targetEntity);
            followUpEvents.add(hpEvent);
        }
        if (entityWorld.hasComponents(spell, DisplacementComponent.class)) {
            DisplacementComponent displacement = entityWorld.getComponent(spell, DisplacementComponent.class);
            PositionComponent posSource = entityWorld.getComponent(playerEntity, PositionComponent.class);
            followUpEvents.add(new DisplacementEvent(targetEntity, displacement.getDisplacement(), posSource.getX(), posSource.getY()));
        }
        if (entityWorld.hasComponents(spell, AttackPointsBuffComponent.class) || entityWorld.hasComponents(spell, MovementPointBuffComponent.class)
                || entityWorld.hasComponents(spell, HealthPointBuffComponent.class)) {

            followUpEvents.add(new PlayerBuffAddedEvent(playerEntity, spell));
        }
        if (entityWorld.hasComponents(spell, HealBuffComponent.class)) {
            HealBuffComponent component = entityWorld.getComponent(spell, HealBuffComponent.class);
            Event event2 = component.isSpellBuff() ? new SpellBuffAddedEvent(spell) : new PlayerBuffAddedEvent(playerEntity, spell);

            if (!followUpEvents.contains(event2)) {
                followUpEvents.add(event);
            }
        }
        if (entityWorld.hasComponents(spell, DamageBuffComponent.class)) {
            DamageBuffComponent component = entityWorld.getComponent(spell, DamageBuffComponent.class);
            Event event2 = component.isSpellBuff() ? new SpellBuffAddedEvent(spell) : new PlayerBuffAddedEvent(playerEntity, spell);
            if (!followUpEvents.contains(event2)) {
                followUpEvents.add(event);
            }
        }
        if (entityWorld.hasComponents(spell, AttackPointsPerTurnComponent.class) || entityWorld.hasComponents(spell, MovementPointsPerTurnComponent.class)
                || entityWorld.hasComponents(spell, HealthPointsPerTurnComponent.class)) {
            followUpEvents.add(new PoisonAddedEvent(playerEntity, targetEntity, spell));
        }
        if (entityWorld.hasComponents(spell, TeleportComponent.class)) {
            followUpEvents.add(new MoveEvent(playerEntity, new PositionComponent(event.getX(), event.getY()), MoveType.TELEPORT));
        }

        eventbusInstance.registerSubEvents(followUpEvents);
    }


}
