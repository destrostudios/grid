package com.destrostudios.grid.actions;

import com.destrostudios.grid.components.PositionComponent;
import com.destrostudios.grid.eventbus.events.NewEvent;
import com.destrostudios.grid.eventbus.events.PositionChangedEvent;
import com.destrostudios.grid.eventbus.events.RoundSkippedEvent;

public class ActionDispatcher {

    public static NewEvent dispatchAction(Action action) {
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
