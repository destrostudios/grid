package com.destrostudios.grid.eventbus.action.displace;

import com.destrostudios.grid.components.map.PositionComponent;
import com.destrostudios.grid.components.properties.HealthPointsComponent;
import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.eventbus.Event;
import com.destrostudios.grid.eventbus.EventHandler;
import com.destrostudios.grid.eventbus.Eventbus;
import com.destrostudios.grid.eventbus.action.move.MoveEvent;
import com.destrostudios.grid.eventbus.action.move.MoveType;
import com.destrostudios.grid.eventbus.update.hp.HealthPointsChangedEvent;
import com.destrostudios.grid.util.SpellUtils;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@AllArgsConstructor
public class PushHandler implements EventHandler<PushEvent> {
  private final Eventbus eventbus;

  @Override
  public void onEvent(PushEvent event, Supplier<EntityData> entityDataSupplier) {
    EntityData entityData = entityDataSupplier.get();
    int entityToDisplace = event.getEntityToDisplace();
    List<Event> followUpEvents = new ArrayList<>();

    PositionComponent posEntityToDisplace =
        entityData.getComponent(entityToDisplace, PositionComponent.class);

    PositionComponent resultingPos =
        SpellUtils.getDisplacementGoal(
            entityData,
            entityToDisplace,
            posEntityToDisplace,
            event.getDirection(),
            event.getStrength());
    int actualDisplacement =
        Math.max(
            Math.abs(resultingPos.getX() - posEntityToDisplace.getX()),
            Math.abs(resultingPos.getY() - posEntityToDisplace.getY()));
    int displacementDmg = getDisplacementDmg(actualDisplacement, event.getStrength());
    followUpEvents.add(new MoveEvent(event.getEntityToDisplace(), resultingPos, MoveType.PUSHBACK));

    if (displacementDmg != 0) {
      HealthPointsComponent hpComponent =
          entityData.getComponent(entityToDisplace, HealthPointsComponent.class);
      followUpEvents.add(
          new HealthPointsChangedEvent(
              entityToDisplace, hpComponent.getHealth() - displacementDmg));
    }
    eventbus.registerSubEvents(followUpEvents);
  }

  private int getDisplacementDmg(int actualDisplacement, int plannedDisplacement) {
    return (plannedDisplacement - actualDisplacement) * 10;
  }
}
