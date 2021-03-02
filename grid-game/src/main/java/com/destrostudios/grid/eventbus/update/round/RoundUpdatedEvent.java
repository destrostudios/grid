package com.destrostudios.grid.eventbus.update.round;

import com.destrostudios.grid.eventbus.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoundUpdatedEvent implements Event {
    private final int entity;
}
