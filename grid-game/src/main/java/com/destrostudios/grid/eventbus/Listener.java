package com.destrostudios.grid.eventbus;

import com.destrostudios.grid.components.Component;
import com.destrostudios.grid.entities.EntityMap;
import com.destrostudios.grid.entities.EntityWorld;

public interface Listener<E extends Component> {

    void handle(ComponentUpdateEvent<E> componentUpdateEvent, EntityWorld world);

    Class<E> supports();
}
