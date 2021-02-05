package com.destrostudios.grid.entities;

import com.destrostudios.grid.components.Component;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class EntityWorld implements EntityData {

    @Getter
    private final Multimap<Integer, Component> world;

    public EntityWorld() {
        this.world = MultimapBuilder.linkedHashKeys().linkedHashSetValues().build();
    }

    /**
     * Creates an entitys with the highest integer which is not used
     *
     * @return entity
     */
    public int createEntity() {
        final Optional<Integer> maxValue = world.keys().stream().max(Integer::compare);
        final int entity = maxValue.orElse(0);
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
        return world.entries().stream()
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
        world.put(entity, component);
    }

    public String printState() {
        return "";
    }
}
