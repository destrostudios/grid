package com.destrostudios.grid.eventbus.action.spellcasted;

import com.destrostudios.grid.components.map.PositionComponent;
import com.destrostudios.grid.components.properties.AttackPointsComponent;
import com.destrostudios.grid.components.properties.HealthPointsComponent;
import com.destrostudios.grid.components.properties.MovementPointsComponent;
import com.destrostudios.grid.components.spells.base.DamageComponent;
import com.destrostudios.grid.components.spells.base.HealComponent;
import com.destrostudios.grid.components.spells.buffs.BuffComponent;
import com.destrostudios.grid.components.spells.buffs.DamageBuffComponent;
import com.destrostudios.grid.components.spells.buffs.HealBuffComponent;
import com.destrostudios.grid.components.spells.limitations.CooldownComponent;
import com.destrostudios.grid.components.spells.limitations.CostComponent;
import com.destrostudios.grid.components.spells.limitations.OnCooldownComponent;
import com.destrostudios.grid.components.spells.movements.DisplacementComponent;
import com.destrostudios.grid.components.spells.movements.TeleportComponent;
import com.destrostudios.grid.components.spells.perturn.AttackPointsPerTurnComponent;
import com.destrostudios.grid.components.spells.perturn.CastsPerTurnComponent;
import com.destrostudios.grid.components.spells.perturn.DamagePerTurnComponent;
import com.destrostudios.grid.components.spells.perturn.MovementPointsPerTurnComponent;
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
import com.destrostudios.grid.eventbus.add.poison.StatsPerTurnEvent;
import com.destrostudios.grid.eventbus.add.spellbuff.SpellBuffAddedEvent;
import com.destrostudios.grid.eventbus.update.ap.AttackPointsChangedEvent;
import com.destrostudios.grid.eventbus.update.hp.HealthPointsChangedEvent;
import com.destrostudios.grid.eventbus.update.mp.MovementPointsChangedEvent;
import com.destrostudios.grid.random.RandomProxy;
import com.destrostudios.grid.util.RangeUtils;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static com.destrostudios.grid.util.RangeUtils.calculateTargetEntity;

@AllArgsConstructor
public class SpellCastedEventHandler implements EventHandler<SpellCastedEvent> {
    private final Eventbus eventbusInstance;
    private final RandomProxy randomProxy;

    @Override
    public void onEvent(SpellCastedEvent event, Supplier<EntityWorld> entityWorldSupplier) {
        EntityWorld entityWorld = entityWorldSupplier.get();
        int spell = event.getSpell();
        int targetEntity = calculateTargetEntity(event.getX(), event.getY(), entityWorld);
        int playerEntity = event.getPlayerEntity();

        if (entityWorld.hasComponents(spell, CooldownComponent.class)) {
            entityWorld.addComponent(spell, new OnCooldownComponent(entityWorld.getComponent(spell, CooldownComponent.class).getCooldown()));
        }

        List<Event> followUpEvents = new ArrayList<>();

        // 1. Costs
        if (entityWorld.hasComponents(event.getSpell(), CostComponent.class)) {
            CostComponent costComponent = entityWorld.getComponent(event.getSpell(), CostComponent.class);
            AttackPointsComponent ap = entityWorld.getComponent(event.getPlayerEntity(), AttackPointsComponent.class);
            MovementPointsComponent mp = entityWorld.getComponent(event.getPlayerEntity(), MovementPointsComponent.class);
            HealthPointsComponent hp = entityWorld.getComponent(event.getPlayerEntity(), HealthPointsComponent.class);

            if (costComponent.getApCost() > 0) {
                followUpEvents.add(new AttackPointsChangedEvent(event.getPlayerEntity(), ap.getAttackPoints() - costComponent.getApCost()));
            }
            if (costComponent.getMpCost() > 0) {
                followUpEvents.add(new MovementPointsChangedEvent(event.getPlayerEntity(), mp.getMovementPoints() - costComponent.getMpCost()));
            }
            if (costComponent.getHpCost() > 0) {
                followUpEvents.add(new HealthPointsChangedEvent(event.getPlayerEntity(), hp.getHealth() - costComponent.getHpCost()));
            }
        }

        // 2. Heals
        if (entityWorld.hasComponents(spell, HealComponent.class)) {
            HealComponent heal = entityWorld.getComponent(spell, HealComponent.class);
            int healAmount = randomProxy.nextInt(heal.getMinHeal(), heal.getMaxHeal());
            followUpEvents.add(new HealReceivedEvent(healAmount + RangeUtils.getBuffAmount(spell, playerEntity, entityWorld, HealBuffComponent.class), targetEntity));
        }

        // 3. Damage
        if (entityWorld.hasComponents(spell, DamageComponent.class)) {
            DamageComponent damage = entityWorld.getComponent(spell, DamageComponent.class);
            int damageAmount = randomProxy.nextInt(damage.getMinDmg(), damage.getMaxDmg());
            List<Integer> affectedEntities = RangeUtils.getAffectedEntities(spell, entityWorld.getComponent(event.getPlayerEntity(), PositionComponent.class),
                    new PositionComponent(event.getX(), event.getY()), entityWorld);
            
            for (Integer affectedEntity : affectedEntities) {
                followUpEvents.add(new DamageTakenEvent(damageAmount + RangeUtils.getBuffAmount(spell, playerEntity, entityWorld, DamageBuffComponent.class), affectedEntity));
            }
        }

        // 4. displacement && Teleport
        if (entityWorld.hasComponents(spell, DisplacementComponent.class) && targetEntity != playerEntity) {
            DisplacementComponent displacement = entityWorld.getComponent(spell, DisplacementComponent.class);
            PositionComponent posSource = entityWorld.getComponent(playerEntity, PositionComponent.class);
            followUpEvents.add(new DisplacementEvent(targetEntity, displacement.getDisplacement(), posSource.getX(), posSource.getY()));
        }
        if (entityWorld.hasComponents(spell, TeleportComponent.class)) {
            followUpEvents.add(new MoveEvent(playerEntity, new PositionComponent(event.getX(), event.getY()), MoveType.TELEPORT));
        }

        // 5. buffs
        List<Event> buffEvents = getBuffEvents(event, entityWorld, spell, playerEntity);
        if (!buffEvents.isEmpty()) {
            followUpEvents.addAll(buffEvents);
        }

        // 6. Stats per turn
        if (entityWorld.hasComponents(spell, AttackPointsPerTurnComponent.class) || entityWorld.hasComponents(spell, MovementPointsPerTurnComponent.class)
                || entityWorld.hasComponents(spell, DamagePerTurnComponent.class)) {
            followUpEvents.add(new StatsPerTurnEvent(playerEntity, targetEntity, spell));
        }
        // update casts
        if (entityWorld.hasComponents(spell, CastsPerTurnComponent.class)) {
            CastsPerTurnComponent castsPerTurnComponent = entityWorld.getComponent(spell, CastsPerTurnComponent.class);
            entityWorld.addComponent(spell, new CastsPerTurnComponent(castsPerTurnComponent.getMaxCastsPerTurn(), castsPerTurnComponent.getCastsThisTurn() - 1));
        }
        eventbusInstance.registerSubEvents(followUpEvents);
    }

    private List<Event> getBuffEvents(SpellCastedEvent event, EntityWorld entityWorld, int spell, int playerEntity) {
        List<Event> followUpEvents = new ArrayList<>();

        boolean hasBuff = entityWorld.getComponents(spell).stream()
                .anyMatch(c -> c instanceof BuffComponent);

        boolean hasSpellBuff = entityWorld.getComponents(spell).stream()
                .filter(c -> c instanceof BuffComponent)
                .map(c -> (BuffComponent) c)
                .anyMatch(BuffComponent::isSpellBuff);

        boolean hasPlayerBuff = entityWorld.getComponents(spell).stream()
                .filter(c -> c instanceof BuffComponent)
                .map(c -> (BuffComponent) c)
                .anyMatch(c -> !c.isSpellBuff());

        if (hasBuff) {
            if (hasSpellBuff) {
                followUpEvents.add(new SpellBuffAddedEvent(spell));
            }
            if (hasPlayerBuff) {
                followUpEvents.add(new PlayerBuffAddedEvent(playerEntity, spell));
            }
        }
        return followUpEvents;
    }


}
