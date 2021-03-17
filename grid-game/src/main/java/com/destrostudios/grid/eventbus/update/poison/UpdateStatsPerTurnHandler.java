package com.destrostudios.grid.eventbus.update.poison;

import com.destrostudios.grid.components.properties.*;
import com.destrostudios.grid.components.properties.resistance.AttackPointResistanceComponent;
import com.destrostudios.grid.components.properties.resistance.MovementPointResistanceComponent;
import com.destrostudios.grid.components.spells.perturn.*;
import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.eventbus.Event;
import com.destrostudios.grid.eventbus.EventHandler;
import com.destrostudios.grid.eventbus.Eventbus;
import com.destrostudios.grid.eventbus.update.ap.AttackPointsChangedEvent;
import com.destrostudios.grid.eventbus.update.hp.HealthPointsChangedEvent;
import com.destrostudios.grid.eventbus.update.mp.MovementPointsChangedEvent;
import com.destrostudios.grid.random.RandomProxy;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@AllArgsConstructor
public class UpdateStatsPerTurnHandler implements EventHandler<UpdateStatsPerTurnEvent> {
    private final Eventbus eventbus;
    private final RandomProxy random;

    @Override
    public void onEvent(UpdateStatsPerTurnEvent event, Supplier<EntityData> entityDataSupplier) {
        EntityData entityData = entityDataSupplier.get();
        List<Event> followUpEvents = handlePoisons(entityData, event.getEntity());
        eventbus.registerSubEvents(followUpEvents);
    }


    private List<Event> handlePoisons(EntityData entityData, int playerEntity) {
        List<Event> followUpEvents = new ArrayList<>();
        int apPoisonSum = 0;
        int mpPoisonSum = 0;
        int hpPoisonSum = 0;

        StatsPerRoundComponent statsPerTurnComp = entityData.getComponent(playerEntity, StatsPerRoundComponent.class);

        for (int statsPerTurn : statsPerTurnComp.getStatsPerRoundEntites()) {
            SourceComponent source = entityData.getComponent(statsPerTurn, SourceComponent.class);
            if (entityData.hasComponents(statsPerTurn, AttackPointsPerTurnComponent.class)) {
                AttackPointsPerTurnComponent apPoison = entityData.getComponent(statsPerTurn, AttackPointsPerTurnComponent.class);
                AttackPointResistanceComponent apResSource = entityData.getComponent(source.getSourceEntity(), AttackPointResistanceComponent.class);
                AttackPointResistanceComponent apResTarget = entityData.getComponent(playerEntity, AttackPointResistanceComponent.class);
                apPoisonSum += getResultingValue(apPoison.getPoisonMaxValue() - apPoison.getPoisonMinValue(),
                        apResSource.getResistanceValue(), apResTarget.getResistanceValue());

            } else if (entityData.hasComponents(statsPerTurn, MovementPointsPerTurnComponent.class)) {
                MovementPointsPerTurnComponent mpPerTurn = entityData.getComponent(statsPerTurn, MovementPointsPerTurnComponent.class);
                MovementPointResistanceComponent mpResSource = entityData.getComponent(source.getSourceEntity(),
                        MovementPointResistanceComponent.class);
                MovementPointResistanceComponent mpResTarget = entityData.getComponent(playerEntity, MovementPointResistanceComponent.class);
                mpPoisonSum += getResultingValue(mpPerTurn.getPoisonMaxValue() - mpPerTurn.getPoisonMinValue(),
                        mpResSource.getResistanceValue(), mpResTarget.getResistanceValue());

            } else if (entityData.hasComponents(statsPerTurn, HealPerTurnComponent.class)) {
                HealPerTurnComponent heal = entityData.getComponent(statsPerTurn, HealPerTurnComponent.class);
                hpPoisonSum += random.nextInt(heal.getHealMinValue(), heal.getHealMaxValue());

            } else if (entityData.hasComponents(statsPerTurn, DamagePerTurnComponent.class)) {
                DamagePerTurnComponent damage = entityData.getComponent(statsPerTurn, DamagePerTurnComponent.class);
                hpPoisonSum  += random.nextInt(damage.getDamageMinValue(), damage.getDamageMaxValue());
            }
        }

        if (apPoisonSum != 0) {
            AttackPointsComponent component = entityData.getComponent(playerEntity, AttackPointsComponent.class);
            followUpEvents.add(new AttackPointsChangedEvent(playerEntity, component.getAttackPoints() + apPoisonSum));
        }
        if (mpPoisonSum != 0) {
            MovementPointsComponent component = entityData.getComponent(playerEntity, MovementPointsComponent.class);
            followUpEvents.add(new MovementPointsChangedEvent(playerEntity, component.getMovementPoints() + mpPoisonSum));
        }
        if (hpPoisonSum != 0) {
            HealthPointsComponent component = entityData.getComponent(playerEntity, HealthPointsComponent.class);
            followUpEvents.add(new HealthPointsChangedEvent(playerEntity, component.getHealth() + hpPoisonSum));
        }
        return followUpEvents;
    }

    private int getSourceEntity(EntityData data, int spellEntity, int playerEntity) { // every spell is unique
        List<Integer> spellsEntities = data.list(SpellsComponent.class).stream()
                .filter(e -> e != playerEntity)
                .collect(Collectors.toList());
        for (int spellsEntity : spellsEntities) {
            SpellsComponent component = data.getComponent(spellsEntity, SpellsComponent.class);
            if (component.getSpells().contains(spellEntity)) {
                return spellsEntity;
            }
        }
        return -1;
    }

    private int getResultingValue(int poisonValue, int resistanceSource, int resistanceTarget) {
        if (resistanceSource == 0) {
            float factor = Math.max(0, 1f - resistanceTarget);
            return (int) (factor * poisonValue);
        } else if (resistanceTarget == 0) {
            return poisonValue;
        } else if (resistanceSource > resistanceTarget) {
            float factor = Math.min(1, (float) resistanceSource / resistanceTarget);
            return (int) (factor * poisonValue);
        }
        float factor = Math.max(0, (float) resistanceSource / resistanceTarget);
        return (int) (factor * poisonValue);
    }
}
