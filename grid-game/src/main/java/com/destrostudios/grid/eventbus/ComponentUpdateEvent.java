package com.destrostudios.grid.eventbus;

import com.destrostudios.grid.components.Component;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ComponentUpdateEvent<T extends Component> {

    private final int entity;
    private final T component;

    public T getComponent() {
        return component;
    }

    public int getEntity() {
        return entity;
    }

}
