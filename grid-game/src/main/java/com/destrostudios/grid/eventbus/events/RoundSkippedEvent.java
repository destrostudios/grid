package com.destrostudios.grid.eventbus.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoundSkippedEvent implements NewEvent {
    private final int entity;

}
