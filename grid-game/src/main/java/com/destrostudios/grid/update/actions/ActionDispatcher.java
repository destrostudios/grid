package com.destrostudios.grid.update.actions;

import com.destrostudios.grid.components.Component;
import com.destrostudios.grid.components.PositionComponent;
import com.destrostudios.grid.update.eventbus.ComponentUpdateEvent;

public class ActionDispatcher {

    public static ComponentUpdateEvent<? extends Component> dispatchAction(Action action) {
        if (action instanceof PositionUpdateAction) {
            PositionUpdateAction posAction = (PositionUpdateAction) action;
            return new ComponentUpdateEvent<>(0, new PositionComponent(posAction.getNewX(),posAction.getNewY()));
        }
        throw new UnsupportedOperationException();
    }

}
