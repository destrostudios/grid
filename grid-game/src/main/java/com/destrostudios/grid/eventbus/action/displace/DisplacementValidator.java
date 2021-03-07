package com.destrostudios.grid.eventbus.action.displace;

import com.destrostudios.grid.components.map.PositionComponent;
import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.eventbus.EventValidator;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DisplacementValidator implements EventValidator<DisplacementEvent> {
    @Override
    public boolean validate(DisplacementEvent event, Supplier<EntityData> entityDataSupplier) {
        List<PositionComponent> relevantPos = Lists.newArrayList(new PositionComponent(event.getXDisplacementSource(), event.getYDisplacementSource()),
                new PositionComponent(event.getXDisplacementSource() + 1, event.getYDisplacementSource()),
                new PositionComponent(event.getXDisplacementSource() - 1, event.getYDisplacementSource()),
                new PositionComponent(event.getXDisplacementSource(), event.getYDisplacementSource() + 1),
                new PositionComponent(event.getXDisplacementSource(), event.getYDisplacementSource() - 1)
        );
        return entityDataSupplier.get().hasComponents(event.getEntityToDisplace(), PositionComponent.class)
                && relevantPos.contains(entityDataSupplier.get().getComponent(event.getEntityToDisplace(), PositionComponent.class));
    }
}
