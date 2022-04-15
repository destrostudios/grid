package com.destrostudios.grid.eventbus.action.die;

import com.destrostudios.grid.components.properties.HealthPointsComponent;
import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.eventbus.EventValidator;

import java.util.function.Supplier;

public class DieEventValidator implements EventValidator<DieEvent> {
  @Override
  public boolean validate(DieEvent event, Supplier<EntityData> entityDataSupplier) {
    return entityDataSupplier
            .get()
            .getComponent(event.getEntity(), HealthPointsComponent.class)
            .getHealth()
        <= 0;
  }
}
