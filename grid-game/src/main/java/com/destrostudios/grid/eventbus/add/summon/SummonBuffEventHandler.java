package com.destrostudios.grid.eventbus.add.summon;

import com.destrostudios.grid.components.properties.LifeSpanComponent;
import com.destrostudios.grid.components.spells.buffs.LifespanBuffComponent;
import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.eventbus.EventHandler;

import java.util.function.Supplier;

public class SummonBuffEventHandler implements EventHandler<SummonBuffEvent> {
  @Override
  public void onEvent(SummonBuffEvent event, Supplier<EntityData> entityDataSupplier) {
    // TODO Summonbuff wie player behandeln?
    EntityData entityData = entityDataSupplier.get();
    int summonEntity = event.getSummonEntity();
    int spell = event.getSpell();

    LifespanBuffComponent lifespanBuffComponent =
        entityData.getComponent(spell, LifespanBuffComponent.class);
    if (lifespanBuffComponent != null
        && entityData.hasComponents(summonEntity, LifeSpanComponent.class)) {
      int buffEntity = entityData.createEntity();
      LifeSpanComponent lifespanComp =
          entityData.getComponent(summonEntity, LifeSpanComponent.class);
      entityData.addComponent(
          buffEntity,
          new LifespanBuffComponent(
              lifespanBuffComponent.getLifespanBuff() + lifespanComp.getRemainingLifeSpan()));
    }
  }
}
