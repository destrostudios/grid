package com.destrostudios.grid.eventbus.events.properties;

import com.destrostudios.grid.eventbus.events.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MaxHealthPointsChangedEvent implements Event {
    private int entity;
    private int maxHealtPoints;
}
