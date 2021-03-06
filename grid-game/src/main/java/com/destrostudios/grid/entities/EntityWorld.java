package com.destrostudios.grid.entities;

import com.destrostudios.grid.components.Component;
import com.destrostudios.grid.serialization.ComponentsContainerSerializer;
import com.destrostudios.grid.serialization.container.GameStateContainer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;


@EqualsAndHashCode
public class EntityWorld implements EntityData {
    private final static Logger logger = Logger.getGlobal();

    @Getter
    private final Map<Integer, List<Component>> world;


    public EntityWorld() {
        this.world = new LinkedHashMap<>();
    }

    public void initializeWorld(String worldState) {
        this.world.clear();
        try {
            GameStateContainer state = ComponentsContainerSerializer.readContainerAsJson(worldState, GameStateContainer.class);
            this.world.putAll(state.getComponents());
        } catch (Exception e) {
            logger.log(Level.WARNING, e, () -> "Couldn´t initialize game state!");
        }
    }

    @Override
    public List<Component> getComponents(int entity) {
        return world.getOrDefault(entity, new ArrayList<>());
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
        world.put(entity, new ArrayList<>());
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
    public <T> T getComponent(int entity, Class<T> component) {
        List<Component> components = world.get(entity);
        if (components == null) {
            return null;
        }
        return components.stream()
                .filter(component::isInstance)
                .map(c -> (T) c)
                .findFirst()
                .orElse(null);
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
    public void removeEntity(int entity) {
        world.remove(entity);
    }

    @Override
    public List<Integer> list(Class<?> component) {
        return world.entrySet().stream()
                .filter(e -> e.getValue().stream().anyMatch(component::isInstance))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasEntity(int entity) {
        return world.containsKey(entity);
    }


    public <E extends Component> List<E> listComponents(Class<E> component) {
        List<Integer> entities = list(component);
        return entities.stream()
                .map(entity -> getComponent(entity, component))
                .filter(Objects::nonNull)
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
