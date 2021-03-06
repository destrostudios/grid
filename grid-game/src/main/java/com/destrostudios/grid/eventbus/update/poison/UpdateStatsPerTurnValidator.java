package com.destrostudios.grid.eventbus.update.poison;

import com.destrostudios.grid.components.properties.StatsPerRoundComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.EventValidator;

import java.util.function.Supplier;

public class UpdateStatsPerTurnValidator implements EventValidator<UpdateStatsPerTurnEvent> {
    @Override
    public boolean validate(UpdateStatsPerTurnEvent event, Supplier<EntityWorld> entityWorldSupplier) {
        return entityWorldSupplier.get().hasComponents(event.getEntity(), StatsPerRoundComponent.class)
                && !entityWorldSupplier.get().getComponent(event.getEntity(), StatsPerRoundComponent.class).getStatsPerRoundEntites().isEmpty();
    }
}
