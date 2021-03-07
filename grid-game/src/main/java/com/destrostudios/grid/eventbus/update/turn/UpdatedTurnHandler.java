package com.destrostudios.grid.eventbus.update.turn;

import com.destrostudios.grid.components.character.PlayerComponent;
import com.destrostudios.grid.components.character.TurnComponent;
import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.eventbus.EventHandler;
import com.destrostudios.grid.eventbus.Eventbus;
import com.destrostudios.grid.eventbus.action.beginturn.BeginTurnEvent;
import com.destrostudios.grid.eventbus.action.endturn.EndTurnEvent;
import java.util.List;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UpdatedTurnHandler implements EventHandler<UpdatedTurnEvent> {

    private final Eventbus instance;

    @Override
    public void onEvent(UpdatedTurnEvent event, Supplier<EntityData> entityDataSupplier) {
        EntityData entityData = entityDataSupplier.get();
        int currentEntity = event.getEntity();
        entityData.remove(currentEntity, TurnComponent.class);

        int newActivePlayer = entityData.list(PlayerComponent.class).stream()
                .filter(i -> i != currentEntity)
                .findFirst()
                .orElse(-1);
        entityData.addComponent(newActivePlayer, new TurnComponent());
        instance.registerSubEvents(List.of(new EndTurnEvent(currentEntity), new BeginTurnEvent(newActivePlayer)));
    }
}
