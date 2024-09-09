package com.destrostudios.grid.eventbus.add.poison;

import com.destrostudios.grid.components.properties.StatsPerRoundComponent;
import com.destrostudios.grid.components.spells.perturn.*;
import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.eventbus.EventHandler;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@AllArgsConstructor
public class StatsPerTurnHandler implements EventHandler<StatsPerTurnEvent> {

  @Override
  public void onEvent(StatsPerTurnEvent event, Supplier<EntityData> entityDataSupplier) {
    EntityData entityData = entityDataSupplier.get();
    int spell = event.getSpellEntity();
    List<Integer> statsPerRound = entityData.hasComponents(event.getTargetEntity(), StatsPerRoundComponent.class)
            ? new ArrayList(entityData.getComponent(event.getTargetEntity(), StatsPerRoundComponent.class).getStatsPerRoundEntites())
            : new ArrayList<>();

    if (entityData.hasComponents(spell, AttackPointsPerTurnComponent.class)) {
      int activePoison = entityData.createEntity();
      AttackPointsPerTurnComponent apPoison =
          entityData.getComponent(event.getSpellEntity(), AttackPointsPerTurnComponent.class);
      entityData.addComponent(
          activePoison,
          new AttackPointsPerTurnComponent(
              apPoison.getMinValue(), apPoison.getMaxValue(), apPoison.getDuration()));
      statsPerRound.add(activePoison);
    }
    if (entityData.hasComponents(spell, MovementPointsPerTurnComponent.class)) {
      int activePoison = entityData.createEntity();
      MovementPointsPerTurnComponent mpPoison =
          entityData.getComponent(event.getSpellEntity(), MovementPointsPerTurnComponent.class);
      entityData.addComponent(
          activePoison,
          new MovementPointsPerTurnComponent(
              mpPoison.getMinValue(), mpPoison.getMaxValue(), mpPoison.getDuration()));
      statsPerRound.add(activePoison);
    }
    if (entityData.hasComponents(spell, DamagePerTurnComponent.class)) {
      int activePoison = entityData.createEntity();
      DamagePerTurnComponent hpPoison =
          entityData.getComponent(event.getSpellEntity(), DamagePerTurnComponent.class);
      entityData.addComponent(
          activePoison,
          new DamagePerTurnComponent(
              hpPoison.getMinValue(), hpPoison.getMaxValue(), hpPoison.getDuration()));
      statsPerRound.add(activePoison);
    }
    if (entityData.hasComponents(spell, HealPerTurnComponent.class)) {
      int activePoison = entityData.createEntity();
      HealPerTurnComponent healPerTurn =
          entityData.getComponent(event.getSpellEntity(), HealPerTurnComponent.class);
      entityData.addComponent(
          activePoison,
          new HealPerTurnComponent(
              healPerTurn.getMinValue(), healPerTurn.getMaxValue(), healPerTurn.getDuration()));
      statsPerRound.add(activePoison);
    }

    entityData.addComponent(event.getTargetEntity(), new StatsPerRoundComponent(statsPerRound));
    entityData.addComponent(event.getTargetEntity(), new SourceComponent(event.getSourceEntity()));
  }
}
