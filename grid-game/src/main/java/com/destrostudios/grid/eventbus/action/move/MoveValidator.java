package com.destrostudios.grid.eventbus.action.move;

import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.eventbus.EventValidator;
import java.util.function.Supplier;

import static com.destrostudios.grid.util.RangeUtils.isPositionIsFree;

public class MoveValidator implements EventValidator<MoveEvent> {

    @Override
    public boolean validate(MoveEvent event, Supplier<EntityData> entityDataSupplier) {
        return isPositionIsFree(entityDataSupplier.get(), event.getPositionComponent(), event.getEntity());
    }
}
