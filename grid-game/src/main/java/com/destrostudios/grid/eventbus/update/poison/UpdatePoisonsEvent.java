package com.destrostudios.grid.eventbus.update.poison;

import com.destrostudios.grid.eventbus.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UpdatePoisonsEvent implements Event {
    private final int entity;
}
