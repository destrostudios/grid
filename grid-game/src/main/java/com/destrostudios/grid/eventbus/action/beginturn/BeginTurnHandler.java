package com.destrostudios.grid.eventbus.action.beginturn;

import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.eventbus.Event;
import com.destrostudios.grid.eventbus.EventHandler;
import com.destrostudios.grid.eventbus.Eventbus;
import com.destrostudios.grid.eventbus.update.playerenchantments.UpdatePlayerEnchantmentsEvent;
import com.destrostudios.grid.eventbus.update.spells.UpdateSpellsEvent;
import com.destrostudios.grid.eventbus.update.statsperturn.UpdateStatsPerTurnEvent;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@AllArgsConstructor
public class BeginTurnHandler implements EventHandler<BeginTurnEvent> {
  private final Eventbus eventbus;

  @Override
  public void onEvent(BeginTurnEvent event, Supplier<EntityData> entityDataSupplier) {
    int currentEntity = event.getBeginTurnEntity();

    List<Event> followUpEvents = new ArrayList<>();

    // update spells, buffs and poisons at the beginning of
    followUpEvents.add(new UpdateSpellsEvent(currentEntity));
    followUpEvents.add(new UpdatePlayerEnchantmentsEvent(currentEntity));
    followUpEvents.add(new UpdateStatsPerTurnEvent(currentEntity));

    eventbus.registerSubEvents(followUpEvents);
  }
}
