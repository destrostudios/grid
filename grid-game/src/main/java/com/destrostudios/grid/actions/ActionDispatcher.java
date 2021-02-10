package com.destrostudios.grid.actions;

import com.destrostudios.grid.components.Component;
import com.destrostudios.grid.components.PositionComponent;
import com.destrostudios.grid.components.RoundComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.ComponentEventBus;
import com.destrostudios.grid.eventbus.ComponentUpdateEvent;
import com.destrostudios.grid.eventbus.Listener;

public class ActionDispatcher {
    private final ComponentEventBus eventBus;

    public ActionDispatcher() {
        this.eventBus = new ComponentEventBus();
    }

    public void addListener(Listener<? extends Component> listener) {
        this.eventBus.register(listener);
    }

    public void dispatchAction(Action action, EntityWorld world) {
        int entity = Integer.parseInt(action.getPlayerIdentifier());
        if (action instanceof PositionUpdateAction) {
            PositionUpdateAction posAction = (PositionUpdateAction) action;
            eventBus.publish(new ComponentUpdateEvent<>(entity, new PositionComponent(posAction.getNewX(), posAction.getNewY())), world);
        } else if (action instanceof SkipRoundAction) {
            eventBus.publish(new ComponentUpdateEvent<>(entity, new RoundComponent()), world);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public void removeListener(Listener<Component> listener) {
        this.eventBus.unregister(listener);
    }
}
