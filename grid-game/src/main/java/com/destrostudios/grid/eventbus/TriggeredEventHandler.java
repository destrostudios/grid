package com.destrostudios.grid.eventbus;

import com.destrostudios.grid.eventbus.events.Event;
import com.destrostudios.grid.eventbus.handler.EventHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TriggeredEventHandler {
    private Event event;
    private EventHandler eventHandler;
}
