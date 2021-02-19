package com.destrostudios.grid.eventbus.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public abstract class PropertiePointsChangedEvent implements Event {
    private int entity;
    private int newPoints;

    public static class AttackPointsChangedEvent extends PropertiePointsChangedEvent {
        public AttackPointsChangedEvent(int entity, int newPoints) {
            super(entity, newPoints);
        }
    }

    public static class MaxAttackPointsChangedEvent extends PropertiePointsChangedEvent {
        public MaxAttackPointsChangedEvent(int entity, int newPoints) {
            super(entity, newPoints);
        }
    }

    public static class MovementPointsChangedEvent extends PropertiePointsChangedEvent {
        public MovementPointsChangedEvent(int entity, int newPoints) {
            super(entity, newPoints);
        }
    }

    public static class MaxMovementPointsChangedEvent extends PropertiePointsChangedEvent {
        public MaxMovementPointsChangedEvent(int entity, int newPoints) {
            super(entity, newPoints);
        }
    }

    public static class HealthPointsChangedEvent extends PropertiePointsChangedEvent {
        public HealthPointsChangedEvent(int entity, int newPoints) {
            super(entity, newPoints);
        }
    }

    public static class MaxHealthPointsChangedEvent extends PropertiePointsChangedEvent {
        public MaxHealthPointsChangedEvent(int entity, int newPoints) {
            super(entity, newPoints);
        }
    }

}
