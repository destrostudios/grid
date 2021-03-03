package com.destrostudios.grid.eventbus.update.turn;

import com.destrostudios.grid.components.character.PlayerComponent;
import com.destrostudios.grid.components.character.TurnComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.EventHandler;
import com.destrostudios.grid.eventbus.Eventbus;
import com.destrostudios.grid.eventbus.action.beginturn.BeginTurnEvent;
import com.destrostudios.grid.eventbus.action.endturn.EndTurnEvent;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.function.Supplier;

@AllArgsConstructor
public class UpdatedTurnHandler implements EventHandler<UpdatedTurnEvent> {

    private final Eventbus instance;

    @Override
    public void onEvent(UpdatedTurnEvent event, Supplier<EntityWorld> entityWorldSupplier) {
        EntityWorld entityWorld = entityWorldSupplier.get();
        int currentEntity = event.getEntity();
        entityWorld.remove(currentEntity, TurnComponent.class);

        int newActivePlayer = entityWorld.list(PlayerComponent.class).stream()
                .filter(i -> i != currentEntity)
                .findFirst()
                .orElse(-1);
        entityWorld.addComponent(newActivePlayer, new TurnComponent());
        instance.registerSubEvents(List.of(new EndTurnEvent(currentEntity), new BeginTurnEvent(newActivePlayer)));
    }
}
