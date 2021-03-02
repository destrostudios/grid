package com.destrostudios.grid.eventbus.action.teleport;

import com.destrostudios.grid.components.map.PositionComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.EventValidator;

import java.util.function.Supplier;

import static com.destrostudios.grid.util.CalculationUtils.isPositionIsFree;

public class TeleportValidator implements EventValidator<TeleportEvent> {
    @Override
    public boolean validate(TeleportEvent event, Supplier<EntityWorld> entityWorldSupplier) {
        return isPositionIsFree(entityWorldSupplier.get(), new PositionComponent(event.getX(), event.getY()), event.getEntity());
    }
}
