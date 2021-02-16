package com.destrostudios.grid.eventbus.events.properties;

import com.destrostudios.grid.eventbus.events.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MovementPointsChangedEvent implements Event {
    private final int entity;
    private final int movementPoints;

}
