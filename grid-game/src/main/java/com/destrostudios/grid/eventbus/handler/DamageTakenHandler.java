package com.destrostudios.grid.eventbus.handler;

import com.destrostudios.grid.components.HealthPointsComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.events.DamageTakenEvent;

import java.util.Optional;
import java.util.function.Supplier;

public class DamageTakenHandler implements EventHandler<DamageTakenEvent> {

    @Override
    public void onEvent(DamageTakenEvent event, Supplier<EntityWorld> entityWorldSupplier) {
        Optional<HealthPointsComponent> component = entityWorldSupplier.get().getComponent(event.getTargetEntity(), HealthPointsComponent.class);
        entityWorldSupplier.get().remove(event.getTargetEntity(), HealthPointsComponent.class);
        entityWorldSupplier.get().addComponent(event.getTargetEntity(), new HealthPointsComponent (component.get().getHealth() - event.getDamage()));
    }
}
