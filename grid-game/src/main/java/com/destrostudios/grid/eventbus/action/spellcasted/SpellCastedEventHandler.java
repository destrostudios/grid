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
import com.destrostudios.grid.entities.EntityData;
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
    public void onEvent(SpellCastedEvent event, Supplier<EntityData> entityDataSupplier) {
        EntityData entityData = entityDataSupplier.get();
        int spell = event.getSpell();
        int targetEntity = calculateTargetEntity(event.getX(), event.getY(), entityData);
        int playerEntity = event.getPlayerEntity();

        if (entityData.hasComponents(spell, CooldownComponent.class)) {
            entityData.addComponent(spell, new OnCooldownComponent(entityData.getComponent(spell, CooldownComponent.class).getCooldown()));
        }

        List<Event> followUpEvents = new ArrayList<>();

        // 1. Costs
        if (entityData.hasComponents(event.getSpell(), CostComponent.class)) {
            CostComponent costComponent = entityData.getComponent(event.getSpell(), CostComponent.class);
            AttackPointsComponent ap = entityData.getComponent(event.getPlayerEntity(), AttackPointsComponent.class);
            MovementPointsComponent mp = entityData.getComponent(event.getPlayerEntity(), MovementPointsComponent.class);
            HealthPointsComponent hp = entityData.getComponent(event.getPlayerEntity(), HealthPointsComponent.class);

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
        if (entityData.hasComponents(spell, HealComponent.class)) {
            HealComponent heal = entityData.getComponent(spell, HealComponent.class);
            int healAmount = randomProxy.nextInt(heal.getMinHeal(), heal.getMaxHeal());
            followUpEvents.add(new HealReceivedEvent(healAmount + RangeUtils.getBuffAmount(spell, playerEntity, entityData, HealBuffComponent.class), targetEntity));
            ;
        }

        // 3. Damage
        if (entityData.hasComponents(spell, DamageComponent.class)) {
            DamageComponent damage = entityData.getComponent(spell, DamageComponent.class);
            int damageAmount = randomProxy.nextInt(damage.getMinDmg(), damage.getMaxDmg());
            List<Integer> affectedEntities = RangeUtils.getAffectedEntities(spell, entityData.getComponent(event.getPlayerEntity(), PositionComponent.class),
                    new PositionComponent(event.getX(), event.getY()), entityData);

            for (Integer affectedEntity : affectedEntities) {
                followUpEvents.add(new DamageTakenEvent(damageAmount + RangeUtils.getBuffAmount(spell, playerEntity, entityData, DamageBuffComponent.class), affectedEntity));
            }
        }

        // 4. displacement && Teleport
        if (entityData.hasComponents(spell, DisplacementComponent.class) && targetEntity != playerEntity) {
            DisplacementComponent displacement = entityData.getComponent(spell, DisplacementComponent.class);
            PositionComponent posSource = entityData.getComponent(playerEntity, PositionComponent.class);
            followUpEvents.add(new DisplacementEvent(targetEntity, displacement.getDisplacement(), posSource.getX(), posSource.getY()));
        }
        if (entityData.hasComponents(spell, TeleportComponent.class)) {
            followUpEvents.add(new MoveEvent(playerEntity, new PositionComponent(event.getX(), event.getY()), MoveType.TELEPORT));
        }

        // 5. buffs
        List<Event> buffEvents = getBuffEvents(event, entityData, spell, playerEntity);
        if (!buffEvents.isEmpty()) {
            followUpEvents.addAll(buffEvents);
        }

        // 6. Stats per turn
        if (entityData.hasComponents(spell, AttackPointsPerTurnComponent.class) || entityData.hasComponents(spell, MovementPointsPerTurnComponent.class)
                || entityData.hasComponents(spell, DamagePerTurnComponent.class)) {
            followUpEvents.add(new StatsPerTurnEvent(playerEntity, targetEntity, spell));
        }
        // update casts
        if (entityData.hasComponents(spell, CastsPerTurnComponent.class)) {
            CastsPerTurnComponent castsPerTurnComponent = entityData.getComponent(spell, CastsPerTurnComponent.class);
            entityData.addComponent(spell, new CastsPerTurnComponent(castsPerTurnComponent.getMaxCastsPerTurn(), castsPerTurnComponent.getCastsThisTurn() - 1));
        }
        eventbusInstance.registerSubEvents(followUpEvents);
    }

    private List<Event> getBuffEvents(SpellCastedEvent event, EntityData entityData, int spell, int playerEntity) {
        List<Event> followUpEvents = new ArrayList<>();

        boolean hasBuff = entityData.getComponents(spell).stream()
                .anyMatch(c -> c instanceof BuffComponent);

        boolean hasSpellBuff = entityData.getComponents(spell).stream()
                .filter(c -> c instanceof BuffComponent)
                .map(c -> (BuffComponent) c)
                .anyMatch(BuffComponent::isSpellBuff);

        boolean hasPlayerBuff = entityData.getComponents(spell).stream()
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
