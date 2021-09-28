package com.destrostudios.grid.eventbus.update.mp;

import com.destrostudios.grid.components.properties.MovementPointsComponent;
import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.eventbus.EventHandler;

import java.util.function.Supplier;
import java.util.logging.Logger;

public class MovementPointsChangedHandler implements EventHandler<MovementPointsChangedEvent> {
  private static final Logger logger =
      Logger.getLogger(MovementPointsChangedHandler.class.getSimpleName());

  @Override
  public void onEvent(MovementPointsChangedEvent event, Supplier<EntityData> entityDataSupplier) {
    entityDataSupplier
        .get()
        .addComponent(event.getEntity(), new MovementPointsComponent(event.getNewPoints()));
  }
}
