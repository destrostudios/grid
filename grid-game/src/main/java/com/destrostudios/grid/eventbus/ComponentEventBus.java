package com.destrostudios.grid.eventbus;

import com.destrostudios.grid.components.Component;
import com.destrostudios.grid.entities.EntityWorld;

import java.util.ArrayList;
import java.util.List;


public class ComponentEventBus {

    private final List<Listener<? extends Component>> listeners;

    public ComponentEventBus() {
        listeners = new ArrayList<>();
    }

    public void register(Listener<? extends Component> listener) {
        listeners.add(listener);
    }

    public void unregister(Listener<? extends Component> listener) {
        listeners.remove(listener);
    }

    public void publish(ComponentUpdateEvent<? extends Component> componentUpdateEvent, EntityWorld world) {
        for (Listener<? extends Component> listener : listeners) {
            if (listener.supports().equals(componentUpdateEvent.getComponent().getClass()) ) {
                listener.handle(helper(componentUpdateEvent), world);
            }
        }
    }

    private <T extends Component> ComponentUpdateEvent<T> helper(ComponentUpdateEvent<? extends Component> componentUpdateEvent) {
        return (ComponentUpdateEvent<T>) componentUpdateEvent;
    }

}
