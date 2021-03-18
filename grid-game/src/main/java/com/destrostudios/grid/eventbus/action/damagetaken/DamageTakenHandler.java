package com.destrostudios.grid.eventbus.action.damagetaken;

import com.destrostudios.grid.components.properties.BuffsComponent;
import com.destrostudios.grid.components.properties.HealthPointsComponent;
import com.destrostudios.grid.components.spells.buffs.ReflectionBuffComponent;
import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.eventbus.EventHandler;
import com.destrostudios.grid.eventbus.Eventbus;
import com.destrostudios.grid.eventbus.update.hp.HealthPointsChangedEvent;
import lombok.AllArgsConstructor;

import java.util.function.Supplier;

@AllArgsConstructor
public class DamageTakenHandler implements EventHandler<DamageTakenEvent> {

    private final Eventbus eventbus;

    @Override
    public void onEvent(DamageTakenEvent event, Supplier<EntityData> entityDataSupplier) {
        EntityData entityData = entityDataSupplier.get();
        HealthPointsComponent component = entityData.getComponent(event.getTargetEntity(), HealthPointsComponent.class);
        int reflectionAmount = getReflectionAmount(event.getTargetEntity(), entityData);

        int health = Math.max(0, component.getHealth() - (event.getDamage() - reflectionAmount));
        if (health >= 0) {
            eventbus.registerSubEvents(new HealthPointsChangedEvent(event.getTargetEntity(), health));
        }
        if (reflectionAmount > 0 && !event.isReflected() && event.getTargetEntity() != event.getSourceEntity()) {
            eventbus.registerSubEvents(new DamageTakenEvent(reflectionAmount, event.getTargetEntity(), event.getSourceEntity(), true));
        }
    }

    private int getReflectionAmount(int targetEntity, EntityData entityData) {
        BuffsComponent buffsComponent = entityData.getComponent(targetEntity, BuffsComponent.class);
        int reflectionAmount = 0;
        for (Integer buffEntity : buffsComponent.getBuffEntities()) {
            if (entityData.hasComponents(buffEntity, ReflectionBuffComponent.class)) {
                ReflectionBuffComponent component = entityData.getComponent(buffEntity, ReflectionBuffComponent.class);
                reflectionAmount += component.getBuffAmount();
            }
        }
        return reflectionAmount;
    }
}
