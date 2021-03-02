package com.destrostudios.grid.eventbus.update.round;

import com.destrostudios.grid.components.character.PlayerComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.EventHandler;
import com.destrostudios.grid.eventbus.Eventbus;
import com.destrostudios.grid.eventbus.action.beginturn.BeginTurnEvent;
import com.destrostudios.grid.eventbus.action.endturn.EndTurnEvent;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.function.Supplier;

@AllArgsConstructor
public class RoundUpdatedHandler implements EventHandler<RoundUpdatedEvent> {

    private final Eventbus instance;

    @Override
    public void onEvent(RoundUpdatedEvent event, Supplier<EntityWorld> entityWorldSupplier) {
        EntityWorld entityWorld = entityWorldSupplier.get();
        int currentEntity = event.getEntity();

        int newActivePlayer = entityWorld.list(PlayerComponent.class).stream()
                .filter(i -> i != currentEntity)
                .findFirst()
                .orElse(-1);

        instance.registerSubEvents(List.of(new EndTurnEvent(currentEntity), new BeginTurnEvent(newActivePlayer)));
    }
}
