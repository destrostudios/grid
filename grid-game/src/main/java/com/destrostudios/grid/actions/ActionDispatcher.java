package com.destrostudios.grid.actions;

import com.destrostudios.grid.components.PositionComponent;
import com.destrostudios.grid.eventbus.events.Event;
import com.destrostudios.grid.eventbus.events.PositionChangedEvent;
import com.destrostudios.grid.eventbus.events.RoundSkippedEvent;

public class ActionDispatcher {

    public static Event dispatchAction(Action action) {
        // TODO: Check if action is valid
        int entity = Integer.parseInt(action.getPlayerIdentifier());
        if (action instanceof PositionUpdateAction) {
            PositionUpdateAction posAction = (PositionUpdateAction) action;
            return new PositionChangedEvent(entity, new PositionComponent(posAction.getNewX(), posAction.getNewY()));
        } else if (action instanceof SkipRoundAction) {
            return new RoundSkippedEvent(entity);
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
