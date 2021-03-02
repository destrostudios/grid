package com.destrostudios.grid.eventbus.update.hp;

import com.destrostudios.grid.eventbus.update.PropertiePointsChangedEvent;

public class HealthPointsChangedEvent extends PropertiePointsChangedEvent {
    public HealthPointsChangedEvent(int entity, int newPoints) {
        super(entity, newPoints);
    }
}
