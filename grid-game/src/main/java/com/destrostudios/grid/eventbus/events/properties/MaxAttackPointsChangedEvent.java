package com.destrostudios.grid.eventbus.events.properties;

import com.destrostudios.grid.eventbus.events.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MaxAttackPointsChangedEvent implements Event {
    private int entity;
    private int newMaxAttackPoints;
}
