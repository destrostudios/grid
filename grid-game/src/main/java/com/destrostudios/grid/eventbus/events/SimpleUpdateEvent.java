package com.destrostudios.grid.eventbus.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class SimpleUpdateEvent implements Event {
    private final int entity;

    public static class BuffsUpdateEvent extends SimpleUpdateEvent {
        public BuffsUpdateEvent(int entity) {
            super(entity);
        }
    }

    public static class UpdateCooldownsUpdateEvent extends SimpleUpdateEvent {
        public UpdateCooldownsUpdateEvent(int entity) {
            super(entity);
        }
    }
}
