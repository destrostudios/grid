package com.destrostudios.grid.eventbus.update.playerenchantments;

import com.destrostudios.grid.components.Component;
import com.destrostudios.grid.components.properties.*;
import com.destrostudios.grid.components.spells.buffs.*;
import com.destrostudios.grid.components.spells.perturn.AttackPointsPerTurnComponent;
import com.destrostudios.grid.components.spells.perturn.DamagePerTurnComponent;
import com.destrostudios.grid.components.spells.perturn.HealPerTurnComponent;
import com.destrostudios.grid.components.spells.perturn.MovementPointsPerTurnComponent;
import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.eventbus.Event;
import com.destrostudios.grid.eventbus.EventHandler;
import com.destrostudios.grid.eventbus.Eventbus;
import com.destrostudios.grid.eventbus.update.maxap.MaxAttackPointsChangedEvent;
import com.destrostudios.grid.eventbus.update.maxhp.MaxHealthPointsChangedEvent;
import com.destrostudios.grid.eventbus.update.maxmp.MaxMovementPointsChangedEvent;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@AllArgsConstructor
public class UpdatePlayerEnchantmentsHandler
    implements EventHandler<UpdatePlayerEnchantmentsEvent> {
  private Eventbus eventbus;

  @Override
  public void onEvent(
      UpdatePlayerEnchantmentsEvent event, Supplier<EntityData> entityDataSupplier) {
    EntityData entityData = entityDataSupplier.get();

    BuffChanges buffChanges = new BuffChanges(0, 0, 0);
    updatePoisons(event, entityData);
    updateBuffs(event, entityData, buffChanges);

    List<Event> events = new ArrayList<>();
    if (buffChanges.deltaHP != 0) {
      MaxHealthComponent maxHealth =
          entityData.getComponent(event.getTargetEntity(), MaxHealthComponent.class);
      events.add(
          new MaxHealthPointsChangedEvent(
              event.getTargetEntity(), maxHealth.getMaxHealth() - buffChanges.deltaHP));
    }
    if (buffChanges.deltaAP != 0) {
      MaxAttackPointsComponent maxAp =
          entityData.getComponent(event.getTargetEntity(), MaxAttackPointsComponent.class);
      events.add(
          new MaxAttackPointsChangedEvent(
              event.getTargetEntity(), maxAp.getMaxAttackPoints() - buffChanges.deltaAP));
    }
    if (buffChanges.deltaMP != 0) {
      MaxMovementPointsComponent maxMp =
          entityData.getComponent(event.getTargetEntity(), MaxMovementPointsComponent.class);
      events.add(
          new MaxMovementPointsChangedEvent(
              event.getTargetEntity(), maxMp.getMaxMovementPoints() - buffChanges.deltaMP));
    }
    if (!events.isEmpty()) {
      eventbus.registerSubEvents(events);
    }
  }

  private void updatePoisons(UpdatePlayerEnchantmentsEvent event, EntityData entityData) {
    StatsPerRoundComponent poisons =
        entityData.getComponent(event.getTargetEntity(), StatsPerRoundComponent.class);
    List<Integer> newPoisons = new ArrayList<>(poisons.getStatsPerRoundEntites());
    for (Integer poisonsEntity : poisons.getStatsPerRoundEntites()) {
      boolean removed = updateOrRemove(poisonsEntity, entityData, null);
      if (removed) {
        newPoisons.remove(poisonsEntity);
      }
    }
    entityData.addComponent(event.getTargetEntity(), new StatsPerRoundComponent(newPoisons));
  }

  private void updateBuffs(
      UpdatePlayerEnchantmentsEvent event, EntityData entityData, BuffChanges buffChanges) {
    BuffsComponent buffs = entityData.getComponent(event.getTargetEntity(), BuffsComponent.class);
    List<AtomicInteger> newBuffs =
        buffs.getBuffEntities().stream().map(AtomicInteger::new).collect(Collectors.toList());
    for (int buffEntity : buffs.getBuffEntities()) {
      boolean removed = updateOrRemove(buffEntity, entityData, buffChanges);
      if (removed) {
        newBuffs.remove(new AtomicInteger(buffEntity));
      }
    }
    entityData.addComponent(
        event.getTargetEntity(),
        new BuffsComponent(newBuffs.stream().map(AtomicInteger::get).collect(Collectors.toList())));
  }

  private boolean updateOrRemove(int entity, EntityData entityData, BuffChanges buffChanges) {
    Component componentToUpdate = null;
    boolean remove = false;
    if (entityData.hasComponents(entity, AttackPointsBuffComponent.class)) {
      AttackPointsBuffComponent apBuff =
          entityData.getComponent(entity, AttackPointsBuffComponent.class);
      remove = apBuff.getBuffDuration() == 1;
      componentToUpdate =
          new AttackPointsBuffComponent(
              apBuff.getBuffAmount(), apBuff.getBuffDuration() - 1, apBuff.getBuffType());

    } else if (entityData.hasComponents(entity, HealthPointBuffComponent.class)) {
      HealthPointBuffComponent hpBuff =
          entityData.getComponent(entity, HealthPointBuffComponent.class);
      remove = hpBuff.getBuffDuration() == 1;
      componentToUpdate =
          new HealthPointBuffComponent(
              hpBuff.getBuffAmount(), hpBuff.getBuffDuration() - 1, hpBuff.getBuffType());

    } else if (entityData.hasComponents(entity, ReflectionBuffComponent.class)) {
      ReflectionBuffComponent reflectionBuffComponent =
          entityData.getComponent(entity, ReflectionBuffComponent.class);
      remove = reflectionBuffComponent.getBuffDuration() == 1;
      componentToUpdate =
          new ReflectionBuffComponent(
              reflectionBuffComponent.getBuffAmount(),
              reflectionBuffComponent.getBuffDuration() - 1,
              reflectionBuffComponent.getBuffType());

    } else if (entityData.hasComponents(entity, MovementPointBuffComponent.class)) {
      MovementPointBuffComponent mpBuff =
          entityData.getComponent(entity, MovementPointBuffComponent.class);
      remove = mpBuff.getBuffDuration() == 1;
      componentToUpdate =
          new MovementPointBuffComponent(
              mpBuff.getBuffAmount(), mpBuff.getBuffDuration() - 1, mpBuff.getBuffType());

    } else if (entityData.hasComponents(entity, DamageBuffComponent.class)) {
      DamageBuffComponent mpBuff = entityData.getComponent(entity, DamageBuffComponent.class);
      remove = mpBuff.getBuffDuration() == 1;
      componentToUpdate =
          new DamageBuffComponent(
              mpBuff.getBuffAmount(), mpBuff.getBuffDuration() - 1, mpBuff.getBuffType());

    } else if (entityData.hasComponents(entity, HealBuffComponent.class)) {
      HealBuffComponent healBuff = entityData.getComponent(entity, HealBuffComponent.class);
      remove = healBuff.getBuffDuration() == 1;
      componentToUpdate =
          new HealBuffComponent(
              healBuff.getBuffAmount(), healBuff.getBuffDuration() - 1, healBuff.getBuffType());

    } else if (entityData.hasComponents(entity, AntiCCBuffComponent.class)) {
      AntiCCBuffComponent antiCc = entityData.getComponent(entity, AntiCCBuffComponent.class);
      remove = antiCc.getBuffDuration() == 1;
      componentToUpdate =
          new HealBuffComponent(
              antiCc.getBuffAmount(), antiCc.getBuffDuration() - 1, antiCc.getBuffType());

    } else if (entityData.hasComponents(entity, AttackPointsPerTurnComponent.class)) {
      AttackPointsPerTurnComponent apPoison =
          entityData.getComponent(entity, AttackPointsPerTurnComponent.class);
      remove = apPoison.getDuration() == 1;
      componentToUpdate =
          new AttackPointsPerTurnComponent(
              apPoison.getMinValue(), apPoison.getMaxValue(), apPoison.getDuration() - 1);

    } else if (entityData.hasComponents(entity, MovementPointsPerTurnComponent.class)) {
      MovementPointsPerTurnComponent mpPoison =
          entityData.getComponent(entity, MovementPointsPerTurnComponent.class);
      remove = mpPoison.getDuration() == 1;
      componentToUpdate =
          new MovementPointsPerTurnComponent(
              mpPoison.getMinValue(), mpPoison.getMaxValue(), mpPoison.getDuration() - 1);

    } else if (entityData.hasComponents(entity, DamagePerTurnComponent.class)) {
      DamagePerTurnComponent hpPoison =
          entityData.getComponent(entity, DamagePerTurnComponent.class);
      remove = hpPoison.getDuration() == 1;
      componentToUpdate =
          new DamagePerTurnComponent(
              hpPoison.getMinValue(), hpPoison.getMaxValue(), hpPoison.getDuration() - 1);

    } else if (entityData.hasComponents(entity, HealPerTurnComponent.class)) {
      HealPerTurnComponent heal = entityData.getComponent(entity, HealPerTurnComponent.class);
      remove = heal.getDuration() == 1;
      componentToUpdate =
          new HealPerTurnComponent(heal.getMinValue(), heal.getMaxValue(), heal.getDuration() - 1);
    }
    if (componentToUpdate != null) {
      if (remove) {
        entityData.remove(entity, componentToUpdate.getClass());

        if (buffChanges != null
            && componentToUpdate.getClass().equals(AttackPointsBuffComponent.class)) {
          buffChanges.deltaAP += ((AttackPointsBuffComponent) componentToUpdate).getBuffAmount();
        } else if (buffChanges != null
            && componentToUpdate.getClass().equals(MovementPointBuffComponent.class)) {
          buffChanges.deltaMP += ((MovementPointBuffComponent) componentToUpdate).getBuffAmount();
        } else if (buffChanges != null
            && componentToUpdate.getClass().equals(HealthPointBuffComponent.class)) {
          buffChanges.deltaHP += ((HealthPointBuffComponent) componentToUpdate).getBuffAmount();
        }
      } else {
        entityData.addComponent(entity, componentToUpdate);
      }
    }
    return remove;
  }

  @AllArgsConstructor
  private static class BuffChanges {
    int deltaAP;
    int deltaMP;
    int deltaHP;
  }
}
