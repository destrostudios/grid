package com.destrostudios.grid.entities;

import com.destrostudios.grid.components.Component;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;


public class EntityWorld implements EntityData {

    @Getter
    private final Map<Integer, List<Component>> world;

    public EntityWorld(Map<Integer, List<Component>> world) {
        this.world = world;
    }

    public EntityWorld() {
        this.world = new LinkedHashMap<>();
    }

    /**
     * Creates an entitys with the highest integer which is not used
     *
     * @return entity
     */
    public int createEntity() {
        final Optional<Integer> maxValue = world.keySet().stream().max(Integer::compare);
        final int entity = maxValue.orElse(0) + 1;
        world.put(entity, null);
        return entity;
    }


    /**
     * Search a component, represented by the classz, for an entity
     *
     * @param <T>
     * @param entity
     * @param component
     * @return
     */
    @Override
    public <T> Optional<T> getComponent(int entity, Class<T> component) {
        return world.get(entity).stream()
                .filter(component::isInstance)
                .map(component::cast)
                .findFirst();
    }

    /**
     * removes component from entity
     *
     * @param entity
     * @param component
     */
    @Override
    public void remove(int entity, Class<?> component) {
        Optional<Component> componentOpt = world.get(entity).stream()
                .filter(component::isInstance)
                .findFirst();
        componentOpt.ifPresent(value -> world.remove(entity, value));
    }

    @Override
    public List<Integer> list(Class<?> component) {
        return world.entrySet().stream()
                .filter(e -> component.isInstance(e.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * adds a component for that entity
     *
     * @param entity
     * @param component
     */
    public void addComponent(int entity, Component component) {
        if (component != null) {
            List<Component> components = world.computeIfAbsent(entity, (e) -> new ArrayList<>());
            components.add(component);
        }
    }

}
