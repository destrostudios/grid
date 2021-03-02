package com.destrostudios.grid.eventbus.update.buff;

import com.destrostudios.grid.eventbus.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BuffsUpdateEvent implements Event {
    private final int entity;
}
