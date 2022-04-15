package com.destrostudios.grid.eventbus.action.swap;

import com.destrostudios.grid.components.character.PlayerComponent;
import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.eventbus.EventValidator;

import java.util.function.Supplier;

public class SwapEventValidator implements EventValidator<SwapEvent> {

  @Override
  public boolean validate(SwapEvent event, Supplier<EntityData> entityDataSupplier) {
    return entityDataSupplier.get().hasComponents(event.getTargetEntity(), PlayerComponent.class);
  }
}
