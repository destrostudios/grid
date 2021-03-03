package com.destrostudios.grid.eventbus.action.beginturn;

import com.destrostudios.grid.components.character.TurnComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.Event;
import com.destrostudios.grid.eventbus.EventHandler;
import com.destrostudios.grid.eventbus.Eventbus;
import com.destrostudios.grid.eventbus.action.endturn.EndTurnEvent;
import com.destrostudios.grid.eventbus.update.buff.BuffsUpdateEvent;
import com.destrostudios.grid.eventbus.update.cooldown.UpdateCooldownsUpdateEvent;
import com.destrostudios.grid.eventbus.update.poison.UpdatePoisonsEvent;
import com.destrostudios.grid.eventbus.update.spell.UpdateAcitveDurationSpellsEvent;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@AllArgsConstructor
public class BeginTurnHandler implements EventHandler<BeginTurnEvent> {
    private final Eventbus eventbus;

    @Override
    public void onEvent(BeginTurnEvent event, Supplier<EntityWorld> entityWorldSupplier) {
        EntityWorld entityWorld = entityWorldSupplier.get();
        int currentEntity = event.getBeginTurnEntity();

        List<Event> followUpEvents = new ArrayList<>();

        // update cooldowns, buffs and poisoins at the beginning of
        followUpEvents.add(new BuffsUpdateEvent(currentEntity));
        followUpEvents.add(new UpdatePoisonsEvent(currentEntity));
        followUpEvents.add(new UpdateCooldownsUpdateEvent(currentEntity));
        followUpEvents.add(new UpdateAcitveDurationSpellsEvent(currentEntity));

        eventbus.registerSubEvents(followUpEvents);
    }
}
