package com.destrostudios.grid.eventbus.update.turn;

import com.destrostudios.grid.components.character.ActiveTurnComponent;
import com.destrostudios.grid.components.character.NextTurnComponent;
import com.destrostudios.grid.entities.EntityData;
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
    public void onEvent(UpdatedTurnEvent event, Supplier<EntityData> entityDataSupplier) {
        EntityData entityData = entityDataSupplier.get();
        int activePlayer = entityData.list(ActiveTurnComponent.class).get(0);
        entityData.remove(activePlayer, ActiveTurnComponent.class);
        NextTurnComponent nextTurn = entityData.getComponent(activePlayer, NextTurnComponent.class);

        entityData.addComponent(nextTurn.getNextPlayer(), new ActiveTurnComponent());
        instance.registerSubEvents(List.of(new EndTurnEvent(activePlayer), new BeginTurnEvent(nextTurn.getNextPlayer())));
    }
}
