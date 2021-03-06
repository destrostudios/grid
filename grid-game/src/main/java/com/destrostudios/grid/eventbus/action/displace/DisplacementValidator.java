package com.destrostudios.grid.eventbus.action.displace;

import com.destrostudios.grid.components.map.PositionComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.EventValidator;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.function.Supplier;

@AllArgsConstructor
public class DisplacementValidator implements EventValidator<DisplacementEvent> {
    @Override
    public boolean validate(DisplacementEvent event, Supplier<EntityWorld> entityWorldSupplier) {
        List<PositionComponent> relevantPos = Lists.newArrayList(new PositionComponent(event.getXDisplacementSource(), event.getYDisplacementSource()),
                new PositionComponent(event.getXDisplacementSource() + 1, event.getYDisplacementSource()),
                new PositionComponent(event.getXDisplacementSource() - 1, event.getYDisplacementSource()),
                new PositionComponent(event.getXDisplacementSource(), event.getYDisplacementSource() + 1),
                new PositionComponent(event.getXDisplacementSource(), event.getYDisplacementSource() - 1)
        );
        return entityWorldSupplier.get().hasComponents(event.getEntityToDisplace(), PositionComponent.class)
                && relevantPos.contains(entityWorldSupplier.get().getComponent(event.getEntityToDisplace(), PositionComponent.class));
    }
}
