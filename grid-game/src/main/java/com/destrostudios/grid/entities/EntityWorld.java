package com.destrostudios.grid.entities;

import com.destrostudios.grid.components.Component;
import com.destrostudios.grid.components.PositionComponent;
import com.destrostudios.grid.gamestate.GameState;
import com.destrostudios.grid.gamestate.GameStateConverter;
import lombok.Getter;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


public class EntityWorld implements EntityData {
    private final static Logger logger = Logger.getGlobal();

    @Getter
    private final Map<Integer, List<Component>> world;
    @Getter
    private final EntityMap map;

    public EntityWorld(EntityMap map) {
        this.world = new LinkedHashMap<>();
        this.map = map;
    }

    public void initializeWorld(String worldState) {
        this.world.clear();
        System.out.println(worldState);
        try {
            GameState state = GameStateConverter.unmarshal(worldState);
            Map<Integer, List<Component>> entities = state.getWorld().entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getComponents()));
            this.world.putAll(entities);
            this.map.setMap(state.getMap());
        } catch (Exception e) {
            logger.log(Level.WARNING, e, () -> "Couldn´t initialize game state!");
        }
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
        List<Component> components = world.get(entity);
        if (components == null) {
            return Optional.empty();
        }
        return components.stream()
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

    public void addToMap(int entity, PositionComponent component) {
        this.map.addEntityToMap(entity, component);
    }
}
