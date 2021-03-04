package com.destrostudios.grid.eventbus.action.damagetaken;

import com.destrostudios.grid.components.properties.HealthPointsComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.Eventbus;
import com.destrostudios.grid.eventbus.update.hp.HealthPointsChangedEvent;
import com.destrostudios.grid.eventbus.EventHandler;
import lombok.AllArgsConstructor;

import java.util.function.Supplier;

@AllArgsConstructor
public class DamageTakenHandler implements EventHandler<DamageTakenEvent> {

    private final Eventbus eventbus;

    @Override
    public void onEvent(DamageTakenEvent event, Supplier<EntityWorld> entityWorldSupplier) {
        HealthPointsComponent component = entityWorldSupplier.get().getComponent(event.getTargetEntity(), HealthPointsComponent.class);
        eventbus.registerSubEvents(new HealthPointsChangedEvent(event.getTargetEntity(), Math.max(0, component.getHealth() - event.getDamage())));
    }
}