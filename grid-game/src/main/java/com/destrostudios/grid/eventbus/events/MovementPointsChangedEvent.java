package com.destrostudios.grid.eventbus.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MovementPointsChangedEvent implements NewEvent {
    private final int entity;
    private final int movementPoints;

}
