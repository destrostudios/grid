package com.destrostudios.grid.eventbus.action.displace;

import com.destrostudios.grid.components.character.PlayerComponent;
import com.destrostudios.grid.components.map.PositionComponent;
import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.eventbus.EventValidator;
import java.util.function.Supplier;

import com.destrostudios.grid.util.Direction;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PushValidator implements EventValidator<PushEvent> {
    @Override
    public boolean validate(PushEvent event, Supplier<EntityData> entityDataSupplier) {
        return entityDataSupplier.get().hasComponents(event.getEntityToDisplace(), PlayerComponent.class)
                && entityDataSupplier.get().hasComponents(event.getEntityToDisplace(), PositionComponent.class)
                && event.getStrength() > 0
                && event.getDirection() != Direction.NONE;
    }
}
