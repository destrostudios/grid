package com.destrostudios.grid.entities;

import com.destrostudios.grid.components.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public interface EntityData {
    int createEntity();

    <T> T getComponent(int entity, Class<T> component);

    void addComponent(int entity, Component value); // component type is value.getClass()

    void remove(int entity, Class<?> component);

    List<Integer> list(Class<?> component); // all entities which have the specified component

    default List<Integer> list(Class<?>... components) {
        return Arrays.stream(components)
                .flatMap(c -> list(c).stream())
                .filter(e -> hasComponents(e, components))
                .distinct()
                .collect(Collectors.toList());
    }

    default boolean hasComponents(int entity, Class<?>... classz) {
        for (Class<?> aClass : classz) {
            if (getComponent(entity, aClass) == null) {
                return false;
            }
        }
        return true;
    }

}
