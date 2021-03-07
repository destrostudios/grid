package com.destrostudios.grid.eventbus.update.poison;

import com.destrostudios.grid.components.properties.AttackPointsComponent;
import com.destrostudios.grid.components.properties.HealthPointsComponent;
import com.destrostudios.grid.components.properties.MovementPointsComponent;
import com.destrostudios.grid.components.properties.SpellsComponent;
import com.destrostudios.grid.components.properties.StatsPerRoundComponent;
import com.destrostudios.grid.components.properties.resistence.AttackPointResistenceComponent;
import com.destrostudios.grid.components.properties.resistence.MovementPointResistenceComponent;
import com.destrostudios.grid.components.spells.perturn.AttackPointsPerTurnComponent;
import com.destrostudios.grid.components.spells.perturn.DamagePerTurnComponent;
import com.destrostudios.grid.components.spells.perturn.HealPerTurnComponent;
import com.destrostudios.grid.components.spells.perturn.MovementPointsPerTurnComponent;
import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.eventbus.Event;
import com.destrostudios.grid.eventbus.EventHandler;
import com.destrostudios.grid.eventbus.Eventbus;
import com.destrostudios.grid.eventbus.update.ap.AttackPointsChangedEvent;
import com.destrostudios.grid.eventbus.update.hp.HealthPointsChangedEvent;
import com.destrostudios.grid.eventbus.update.mp.MovementPointsChangedEvent;
import com.destrostudios.grid.random.RandomProxy;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;

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

        StatsPerRoundComponent poisons = entityData.getComponent(playerEntity, StatsPerRoundComponent.class);

        for (int poison : poisons.getStatsPerRoundEntites()) {
            if (entityData.hasComponents(poison, AttackPointsPerTurnComponent.class)) {
                AttackPointsPerTurnComponent apPoison = entityData.getComponent(poison, AttackPointsPerTurnComponent.class);
                AttackPointResistenceComponent apResSource = entityData.getComponent(apPoison.getSourceEntity(), AttackPointResistenceComponent.class);
                AttackPointResistenceComponent apResTarget = entityData.getComponent(playerEntity, AttackPointResistenceComponent.class);
                apPoisonSum += getResultingValue(apPoison.getPoisonMaxValue() - apPoison.getPoisonMinValue(),
                        apResSource.getResitanceValue(), apResTarget.getResitanceValue());

            } else if (entityData.hasComponents(poison, MovementPointsPerTurnComponent.class)) {
                MovementPointsPerTurnComponent mpPoison = entityData.getComponent(poison, MovementPointsPerTurnComponent.class);
                MovementPointResistenceComponent mpResSource = entityData.getComponent(mpPoison.getSourceEntity(),
                        MovementPointResistenceComponent.class);
                MovementPointResistenceComponent mpResTarget = entityData.getComponent(playerEntity, MovementPointResistenceComponent.class);
                mpPoisonSum += getResultingValue(mpPoison.getPoisonMaxValue() - mpPoison.getPoisonMinValue(),
                        mpResSource.getResitanceValue(), mpResTarget.getResitanceValue());

            } else if (entityData.hasComponents(poison, HealPerTurnComponent.class)) {
                HealPerTurnComponent heal = entityData.getComponent(poison, HealPerTurnComponent.class);
                int bound = Math.abs(heal.getHealMaxValue()) - Math.abs(heal.getHealMinValue()) + 1;
                int delta = Math.abs(heal.getHealMinValue()) + random.nextInt(bound);
                hpPoisonSum = hpPoisonSum + (int) Math.signum(heal.getHealMaxValue()) * delta;

            } else if (entityData.hasComponents(poison, DamagePerTurnComponent.class)) {
                DamagePerTurnComponent hpPoison = entityData.getComponent(poison, DamagePerTurnComponent.class);
                int bound = Math.abs(hpPoison.getDamageMaxValue()) - Math.abs(hpPoison.getDamageMinValue()) + 1;
                int delta = Math.abs(hpPoison.getDamageMinValue()) + random.nextInt(bound);
                hpPoisonSum = hpPoisonSum + (int) Math.signum(hpPoison.getDamageMaxValue()) * delta;
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
