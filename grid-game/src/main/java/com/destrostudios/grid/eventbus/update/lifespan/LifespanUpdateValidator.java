package com.destrostudios.grid.eventbus.update.lifespan;

import com.destrostudios.grid.components.properties.ActiveSummonsComponent;
import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.eventbus.EventValidator;

import java.util.function.Supplier;

public class LifespanUpdateValidator implements EventValidator<LifespanUpdateEvent> {

  @Override
  public boolean validate(LifespanUpdateEvent event, Supplier<EntityData> entityDataSupplier) {
    ActiveSummonsComponent summons =
        entityDataSupplier
            .get()
            .getComponent(event.getPlayerEntity(), ActiveSummonsComponent.class);
    return summons != null && !summons.getActiveSummons().isEmpty();
  }
}
