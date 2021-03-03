package com.destrostudios.grid.eventbus.action.displace;

import com.destrostudios.grid.components.character.PlayerComponent;
import com.destrostudios.grid.components.map.ObstacleComponent;
import com.destrostudios.grid.components.map.PositionComponent;
import com.destrostudios.grid.components.properties.PoisonsComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.EventValidator;
import lombok.AllArgsConstructor;

import java.util.function.Supplier;

@AllArgsConstructor
public class DisplacementValidator implements EventValidator<DisplacementEvent> {
    @Override
    public boolean validate(DisplacementEvent event, Supplier<EntityWorld> entityWorldSupplier) {
        boolean isNonPlayerObstacle = entityWorldSupplier.get().hasComponents(event.getEntityToDisplace(), ObstacleComponent.class)
                && !entityWorldSupplier.get().hasComponents(event.getEntityToDisplace(), PlayerComponent.class);
         return !isNonPlayerObstacle && entityWorldSupplier.get().hasComponents(event.getEntityToDisplace(), PositionComponent.class);
    }
}
