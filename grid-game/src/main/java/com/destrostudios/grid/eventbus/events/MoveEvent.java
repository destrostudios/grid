package com.destrostudios.grid.eventbus.events;

import com.destrostudios.grid.components.PositionComponent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MoveEvent implements Event {
    public final int entity;
    public final PositionComponent positionComponent;

}
