package com.destrostudios.grid.eventbus.update.turn;

import com.destrostudios.grid.components.character.ActiveTurnComponent;
import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.eventbus.EventValidator;

import java.util.function.Supplier;

public class UpdatedTurnValidator implements EventValidator<UpdatedTurnEvent> {
  @Override
  public boolean validate(UpdatedTurnEvent event, Supplier<EntityData> entityDataSupplier) {
    EntityData entityData = entityDataSupplier.get();
    return entityData.hasComponents(event.getEntity(), ActiveTurnComponent.class);
  }
}
