package com.destrostudios.grid.eventbus.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class HealthPointsChangedEvent implements Event {
    private final int entity;
    private final int newHealthPoints;
}