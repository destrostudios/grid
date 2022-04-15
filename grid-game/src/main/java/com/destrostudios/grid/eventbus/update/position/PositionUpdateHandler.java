package com.destrostudios.grid.eventbus.update.position;

import com.destrostudios.grid.components.map.PositionComponent;
import com.destrostudios.grid.components.spells.glyphs.SpellOnTouchComponent;
import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.eventbus.Event;
import com.destrostudios.grid.eventbus.EventHandler;
import com.destrostudios.grid.eventbus.Eventbus;
import com.destrostudios.grid.eventbus.action.spellcasted.SpellCastedEvent;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@AllArgsConstructor
public class PositionUpdateHandler implements EventHandler<PositionUpdateEvent> {
  private final Eventbus eventbusInstance;

  @Override
  public void onEvent(PositionUpdateEvent event, Supplier<EntityData> entityDataSupplier) {
    EntityData entityData = entityDataSupplier.get();
    List<Integer> spellOnTouchEntities =
        entityData.list(SpellOnTouchComponent.class).stream()
            .filter(e -> entityData.hasComponents(e, PositionComponent.class))
            .filter(
                e ->
                    entityData
                        .getComponent(e, PositionComponent.class)
                        .equals(event.getNewPosition()))
            .collect(Collectors.toList());

    entityData.addComponent(event.getEntity(), event.getNewPosition());
    List<Event> followUpEvents = new ArrayList<>();

    for (Integer spellOnTouchEntity : spellOnTouchEntities) {
      SpellOnTouchComponent component =
          entityData.getComponent(spellOnTouchEntity, SpellOnTouchComponent.class);
      // todo support casted spell without a player source entity
      followUpEvents.add(
          new SpellCastedEvent(
              component.getSpell(),
              spellOnTouchEntity,
              event.getNewPosition().getX(),
              event.getNewPosition().getY()));
    }
    eventbusInstance.registerSubEvents(followUpEvents);
  }
}
