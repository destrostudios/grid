package com.destrostudios.grid.eventbus.update.maxhp;

import com.destrostudios.grid.components.properties.MaxHealthComponent;
import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.eventbus.EventHandler;

import java.util.function.Supplier;

public class MaxHealthPointsChangedHandler implements EventHandler<MaxHealthPointsChangedEvent> {
  @Override
  public void onEvent(MaxHealthPointsChangedEvent event, Supplier<EntityData> entityDataSupplier) {
    EntityData entityData = entityDataSupplier.get();
    entityData.remove(event.getEntity(), MaxHealthComponent.class);
    entityData.addComponent(event.getEntity(), new MaxHealthComponent(event.getNewPoints()));
  }
}
