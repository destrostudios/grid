package com.destrostudios.grid.eventbus;

import com.destrostudios.grid.entities.EntityData;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TriggeredEventHandler {
    private Event event;
    private EventHandler eventHandler;

    public void onEvent(Supplier<EntityData> entityWorldSupplier) {
        eventHandler.onEvent(event, entityWorldSupplier);
    }
}
