package com.destrostudios.grid.eventbus.events;

public class UpdateCooldownsEvent extends SimpleEvent {
    public UpdateCooldownsEvent(int entity) {
        super(entity);
    }
}
  