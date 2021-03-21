package com.destrostudios.grid.eventbus.update.hp;

import com.destrostudios.grid.components.properties.HealthPointsComponent;
import com.destrostudios.grid.components.properties.MaxHealthComponent;
import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.eventbus.EventHandler;
import com.destrostudios.grid.eventbus.Eventbus;
import com.destrostudios.grid.eventbus.action.die.DieEvent;
import lombok.AllArgsConstructor;

import java.util.function.Supplier;

@AllArgsConstructor
public class HealthPointsChangedHandler implements EventHandler<HealthPointsChangedEvent> {
    private final Eventbus eventbus;

    @Override
    public void onEvent(HealthPointsChangedEvent event, Supplier<EntityData> entityDataSupplier) {
        EntityData entityData = entityDataSupplier.get();
        MaxHealthComponent maxHealthComponent = entityData.getComponent(event.getEntity(), MaxHealthComponent.class);
        HealthPointsComponent hpComponent = new HealthPointsComponent(Math.min(maxHealthComponent.getMaxHealth(), event.getNewPoints()));
        entityData.addComponent(event.getEntity(), hpComponent);

        if (hpComponent.getHealth() <= 0) {
            eventbus.registerSubEvents(new DieEvent(event.getEntity()));
        }
    }

}
