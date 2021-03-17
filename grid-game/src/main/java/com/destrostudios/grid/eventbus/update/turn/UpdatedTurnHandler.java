package com.destrostudios.grid.eventbus.update.turn;

import com.destrostudios.grid.components.character.ActiveTurnComponent;
import com.destrostudios.grid.components.character.NextTurnComponent;
import com.destrostudios.grid.components.properties.IsAliveComponent;
import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.eventbus.EventHandler;
import com.destrostudios.grid.eventbus.Eventbus;
import com.destrostudios.grid.eventbus.action.beginturn.BeginTurnEvent;
import com.destrostudios.grid.eventbus.action.endturn.EndTurnEvent;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@AllArgsConstructor
public class UpdatedTurnHandler implements EventHandler<UpdatedTurnEvent> {

    private final Eventbus instance;

    @Override
    public void onEvent(UpdatedTurnEvent event, Supplier<EntityData> entityDataSupplier) {
        EntityData entityData = entityDataSupplier.get();
        int activePlayer = entityData.list(ActiveTurnComponent.class).get(0);
        entityData.remove(activePlayer, ActiveTurnComponent.class);
        NextTurnComponent component = getNextTurnComponent(event.getEntity(), entityData);

        entityData.addComponent(component.getNextPlayer(), new ActiveTurnComponent());
        instance.registerSubEvents(List.of(new EndTurnEvent(activePlayer), new BeginTurnEvent(component.getNextPlayer())));
    }

    private NextTurnComponent getNextTurnComponent(Integer currentEntity, EntityData entityData) {
        List<NextTurnComponent> nextTurns = entityData.list(NextTurnComponent.class).stream()
                .map(e -> entityData.getComponent(e, NextTurnComponent.class))
                .collect(Collectors.toList());
        NextTurnComponent currentNextTurn = entityData.getComponent(currentEntity, NextTurnComponent.class);
        nextTurns.remove(currentNextTurn);

        NextTurnComponent result = currentNextTurn;

        do {
            if (entityData.hasComponents(result.getNextPlayer(), IsAliveComponent.class)) {
                return result;
            }
            result = entityData.getComponent(result.getNextPlayer(), NextTurnComponent.class);
            nextTurns.remove(result);

        } while (!nextTurns.isEmpty() && result != null);

        return result;
    }
}
