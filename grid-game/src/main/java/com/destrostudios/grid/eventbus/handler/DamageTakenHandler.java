package com.destrostudios.grid.eventbus.handler;

import com.destrostudios.grid.components.properties.HealthPointsComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.Eventbus;
import com.destrostudios.grid.eventbus.events.DamageTakenEvent;
import com.destrostudios.grid.eventbus.events.PropertiePointsChangedEvent;
import lombok.AllArgsConstructor;

import java.util.Optional;
import java.util.function.Supplier;

@AllArgsConstructor
public class DamageTakenHandler implements EventHandler<DamageTakenEvent> {

    private final Eventbus eventbus;

    @Override
    public void onEvent(DamageTakenEvent event, Supplier<EntityWorld> entityWorldSupplier) {
        HealthPointsComponent component = entityWorldSupplier.get().getComponent(event.getTargetEntity(), HealthPointsComponent.class);
        eventbus.registerSubEvents(new PropertiePointsChangedEvent.HealthPointsChangedEvent(event.getTargetEntity(), Math.max(0, component.getHealth() - event.getDamage())));
    }
}
