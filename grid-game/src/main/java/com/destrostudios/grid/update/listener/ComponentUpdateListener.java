package com.destrostudios.grid.update.listener;

import com.destrostudios.grid.update.ComponentUpdateEvent;
import com.google.common.eventbus.Subscribe;

public interface ComponentUpdateListener<E extends ComponentUpdateEvent<?>> {

    @Subscribe
    void handleEvent(E event);
}
