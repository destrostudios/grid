package com.destrostudios.grid.eventbus.events;

public class UpdateBuffsEvent extends SimpleEvent {
    public UpdateBuffsEvent(int entity) {
        super(entity);
    }
}
