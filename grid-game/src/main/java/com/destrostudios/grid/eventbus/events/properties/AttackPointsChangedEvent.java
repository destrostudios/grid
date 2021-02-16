package com.destrostudios.grid.eventbus.events.properties;

import com.destrostudios.grid.eventbus.events.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AttackPointsChangedEvent implements Event {
    private final int entity;
    private final int newAttackPoints;
}
