package com.destrostudios.grid.eventbus.action.teleport;

import com.destrostudios.grid.eventbus.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TeleportEvent implements Event {
    private final int entity;
    private final int x;
    private final int y;
}
