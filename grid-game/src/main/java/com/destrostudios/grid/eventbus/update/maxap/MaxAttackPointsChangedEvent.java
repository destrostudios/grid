package com.destrostudios.grid.eventbus.update.maxap;

import com.destrostudios.grid.eventbus.update.PropertiePointsChangedEvent;

public class MaxAttackPointsChangedEvent extends PropertiePointsChangedEvent {
    public MaxAttackPointsChangedEvent(int entity, int newPoints) {
        super(entity, newPoints);
    }
}
