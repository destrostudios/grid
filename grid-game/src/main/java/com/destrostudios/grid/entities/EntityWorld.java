package com.destrostudios.grid.entities;

import com.destrostudios.grid.components.Component;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Getter;


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
    @Override
    public int createEntity() {
        Optional<Integer> maxValue = world.keySet().stream().max(Integer::compare);
        int entity = maxValue.orElse(0) + 1;
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
        List<Component> components = world.get(entity);
        if (components == null) {
            return;
        }
        Optional<Component> componentOpt = components.stream()
                .filter(component::isInstance)
                .findFirst();
        componentOpt.ifPresent(components::remove);
    }

    @Override
    public List<Integer> list(Class<?> component) {
        return world.entrySet().stream()
                .filter(e -> e.getValue().stream().anyMatch(component::isInstance))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public <E extends Component> List<E> listComponents(Class<E> component) {
        List<Integer> entities = list(component);
        return entities.stream()
                .map(entity -> getComponent(entity, component))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    /**
     * adds a component for that entity
     *
     * @param entity
     * @param component
     */
    @Override
    public void addComponent(int entity, Component component) {
        if (component != null) {
            remove(entity, component.getClass());
            List<Component> components = world.computeIfAbsent(entity, (e) -> new ArrayList<>());
            components.add(component);
        }
    }

}
