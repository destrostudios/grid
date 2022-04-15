package com.destrostudios.grid.eventbus.update.lifespan;

import com.destrostudios.grid.components.properties.ActiveSummonsComponent;
import com.destrostudios.grid.components.properties.LifeSpanComponent;
import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.eventbus.EventHandler;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;

public class LifespanUpdateHandler implements EventHandler<LifespanUpdateEvent> {

  @Override
  public void onEvent(LifespanUpdateEvent event, Supplier<EntityData> entityDataSupplier) {
    EntityData entityData = entityDataSupplier.get();
    int playerEntity = event.getPlayerEntity();
    ActiveSummonsComponent activeSummons =
        entityData.getComponent(playerEntity, ActiveSummonsComponent.class);
    Set<Integer> newActiveSummons = new LinkedHashSet<>();

    for (Integer activeSummon : activeSummons.getActiveSummons()) {
      LifeSpanComponent lifespan = entityData.getComponent(activeSummon, LifeSpanComponent.class);
      if (lifespan == null) {
      } else if (lifespan.getRemainingLifeSpan() > 1) {
        entityData.addComponent(
            activeSummon, new LifeSpanComponent(lifespan.getRemainingLifeSpan() - 1));
        newActiveSummons.add(activeSummon);
      } else {
        entityData.removeEntity(activeSummon);
      }
    }
    entityData.addComponent(playerEntity, new ActiveSummonsComponent(newActiveSummons));
  }
}
