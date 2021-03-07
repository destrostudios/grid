package com.destrostudios.grid.eventbus.action.damagetaken;

import com.destrostudios.grid.components.properties.HealthPointsComponent;
import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.eventbus.EventHandler;
import com.destrostudios.grid.eventbus.Eventbus;
import com.destrostudios.grid.eventbus.update.hp.HealthPointsChangedEvent;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DamageTakenHandler implements EventHandler<DamageTakenEvent> {

    private final Eventbus eventbus;

    @Override
    public void onEvent(DamageTakenEvent event, Supplier<EntityData> entityDataSupplier) {
        HealthPointsComponent component = entityDataSupplier.get().getComponent(event.getTargetEntity(), HealthPointsComponent.class);
        eventbus.registerSubEvents(new HealthPointsChangedEvent(event.getTargetEntity(), Math.max(0, component.getHealth() - event.getDamage())));
    }
}
