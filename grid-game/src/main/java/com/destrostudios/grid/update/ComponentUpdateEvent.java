package com.destrostudios.grid.update;

import com.destrostudios.grid.components.Component;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ComponentUpdateEvent<E extends Component> {

    private final int entity;
    private final E component;
}
