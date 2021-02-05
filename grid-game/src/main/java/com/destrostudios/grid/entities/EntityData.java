package com.destrostudios.grid.entities;

import com.destrostudios.grid.components.Component;

import java.util.List;
import java.util.Optional;

public interface EntityData {
    int createEntity();

    <T> Optional<T> getComponent(int entity, Class<T> component);

    void addComponent(int entity, Component value); // component type is value.getClass()

    void remove(int entity, Class<?> component);

    List<Integer> list(Class<?> component); // all entities which have the specified component

    default boolean hasComponents(int entity, Class<?>... classz) {
        for (Class<?> aClass : classz) {
            if (getComponent(entity, aClass).isEmpty()) {
                return false;
            }
        }
        return true;
    }

}
