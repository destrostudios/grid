package com.destrostudios.grid.entities;

import com.destrostudios.grid.components.Component;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public interface EntityData {
    int createEntity();

    void removeEntity(int entity);

    <T extends Component> T getComponent(int entity, Class<T> component);

    void addComponent(int entity, Component value); // component type is value.getClass()

    void remove(int entity, Class<?> component);

    List<Integer> list(Class<?> component); // all entities which have the specified component

    List<Integer> findEntitiesByComponent(Component component);

    List<Component> getComponents(int entity);

    default List<Integer> list(Class<? extends Component>... components) {
        return Arrays.stream(components)
                .flatMap(c -> list(c).stream())
                .filter(e -> hasComponents(e, components))
                .distinct()
                .collect(Collectors.toList());
    }

    default boolean hasComponents(int entity, Class<? extends Component>... classz) {
        for (Class<? extends Component> aClass : classz) {
            if (getComponent(entity, aClass) == null) {
                return false;
            }
        }
        return true;
    }

    boolean hasEntity(int entity);

}
