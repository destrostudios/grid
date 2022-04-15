package com.destrostudios.grid.eventbus.update.maxap;

import com.destrostudios.grid.components.properties.MaxAttackPointsComponent;
import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.eventbus.EventHandler;

import java.util.function.Supplier;

public class MaxAttackPointsChangedHandler implements EventHandler<MaxAttackPointsChangedEvent> {
  @Override
  public void onEvent(MaxAttackPointsChangedEvent event, Supplier<EntityData> entityDataSupplier) {
    entityDataSupplier
        .get()
        .addComponent(event.getEntity(), new MaxAttackPointsComponent(event.getNewPoints()));
  }
}
