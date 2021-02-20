package com.destrostudios.grid.eventbus.handler;

import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.events.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Supplier;

@AllArgsConstructor
@Getter
public class TriggeredEventHandler {
    private Event event;
    private EventHandler eventHandler;

    public void onEvent(Supplier<EntityWorld> entityWorldSupplier) {
        eventHandler.onEvent(event, entityWorldSupplier);
    }
}
