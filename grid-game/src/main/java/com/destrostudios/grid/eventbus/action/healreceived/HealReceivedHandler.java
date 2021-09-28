package com.destrostudios.grid.eventbus.action.healreceived;

import com.destrostudios.grid.components.properties.HealthPointsComponent;
import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.eventbus.EventHandler;
import com.destrostudios.grid.eventbus.Eventbus;
import com.destrostudios.grid.eventbus.update.hp.HealthPointsChangedEvent;
import lombok.AllArgsConstructor;

import java.util.function.Supplier;

@AllArgsConstructor
public class HealReceivedHandler implements EventHandler<HealReceivedEvent> {

  private final Eventbus eventbus;

  @Override
  public void onEvent(HealReceivedEvent event, Supplier<EntityData> entityDataSupplier) {
    HealthPointsComponent component =
        entityDataSupplier.get().getComponent(event.getTargetEntity(), HealthPointsComponent.class);
    eventbus.registerSubEvents(
        new HealthPointsChangedEvent(
            event.getTargetEntity(), Math.max(0, component.getHealth() + event.getHeal())));
  }
}
