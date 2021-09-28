package com.destrostudios.grid.eventbus.update.statsperturn;

import com.destrostudios.grid.components.properties.StatsPerRoundComponent;
import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.eventbus.EventValidator;

import java.util.function.Supplier;

public class UpdateStatsPerTurnValidator implements EventValidator<UpdateStatsPerTurnEvent> {
  @Override
  public boolean validate(UpdateStatsPerTurnEvent event, Supplier<EntityData> entityDataSupplier) {
    return entityDataSupplier.get().hasComponents(event.getEntity(), StatsPerRoundComponent.class)
        && !entityDataSupplier
            .get()
            .getComponent(event.getEntity(), StatsPerRoundComponent.class)
            .getStatsPerRoundEntites()
            .isEmpty();
  }
}
