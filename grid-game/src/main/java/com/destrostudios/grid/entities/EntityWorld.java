package com.destrostudios.grid.entities;

import com.destrostudios.grid.components.Component;
import com.destrostudios.grid.serialization.ComponentsContainerSerializer;
import com.destrostudios.grid.serialization.container.GameStateContainer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


@EqualsAndHashCode
public class EntityWorld implements EntityData {
    private final static Logger logger = Logger.getGlobal();

    private final Map<Class<? extends Component>, Map<Integer, ? extends Component>> world;

    // getter & setter for serialization
    @Getter
    @Setter
    private int nextEntity = 1;

    public EntityWorld() {
        this.world = new LinkedHashMap<>();
    }

    public void initializeWorld(String worldState) {
        this.world.clear();
        try {
            GameStateContainer state = ComponentsContainerSerializer.readContainerAsJson(worldState, GameStateContainer.class);
            Map<Integer, List<Component>> components = state.getComponents();
            for (Map.Entry<Integer, List<Component>> entry : components.entrySet()) {
                for (Component component : entry.getValue()) {
                    addComponent(entry.getKey(), component);
                }
            }
            nextEntity = state.getNextEntity();
        } catch (Exception e) {
            logger.log(Level.WARNING, e, () -> "Couldn´t initialize game state!");
        }
    }

    @Override
    public List<Component> getComponents(int entity) {
        return world.values().stream()
                .map(components -> components.get(entity))
                .filter(x -> x != null)
                .collect(Collectors.toList());
    }

    /**
     * Creates an entity with the highest integer which is not used
     *
     * @return entity
     */
    @Override
    public int createEntity() {
        return nextEntity++;
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
        Map<Integer, ? extends Component> components = world.get(component);
        if (components == null) {
            return null;
        }
        return (T) components.get(entity);
    }

    /**
     * removes component from entity
     *
     * @param entity
     * @param component
     */
    @Override
    public void remove(int entity, Class<?> component) {
        Map<Integer, ? extends Component> components = world.get(component);
        if (components == null) {
            return;
        }
        components.remove(entity);
    }

    @Override
    public void removeEntity(int entity) {
        world.values().forEach(components -> components.remove(entity));
    }

    @Override
    public List<Integer> list(Class<?> component) {
        Map<Integer, ? extends Component> components = world.get(component);
        if (components == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(components.keySet());
    }

    @Override
    public boolean hasEntity(int entity) {
        return world.values().stream().anyMatch(components -> components.containsKey(entity));
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
            Map components = world.computeIfAbsent(component.getClass(), x -> new LinkedHashMap<>());
            components.put(entity, component);
        }
    }

    // for serialization
    public Map<Integer, List<Component>> getWorld() {
        Map<Integer, List<Component>> result = new LinkedHashMap<>();
        for (Map<Integer, ? extends Component> components : world.values()) {
            for (Map.Entry<Integer, ? extends Component> entry : components.entrySet()) {
                result.computeIfAbsent(entry.getKey(), x -> new ArrayList<>()).add(entry.getValue());
            }
        }
        return Collections.unmodifiableMap(result);
    }
}
