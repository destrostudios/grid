package com.destrostudios.grid.eventbus.update.ap;

import com.destrostudios.grid.eventbus.update.PropertiePointsChangedEvent;

public class AttackPointsChangedEvent extends PropertiePointsChangedEvent {
    public AttackPointsChangedEvent(int entity, int newPoints) {
        super(entity, newPoints);
    }
}
