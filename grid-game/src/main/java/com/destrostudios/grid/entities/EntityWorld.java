package com.destrostudios.grid.entities;

import com.destrostudios.grid.components.Component;
import com.destrostudios.grid.serialization.ComponentsContainerSerializer;
import com.destrostudios.grid.serialization.container.GameStateContainer;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class EntityWorld implements EntityData {
  private static final Logger logger = Logger.getGlobal();

  private final Map<Class<? extends Component>, ComponentTable<?>> world;

  // getter & setter for serialization
  @Getter @Setter private int nextEntity = 1;

  public EntityWorld() {
    this.world = new HashMap<>();
  }

  public void initializeWorld(String worldState) {
    this.world.clear();
    try {
      GameStateContainer state =
          ComponentsContainerSerializer.readContainerAsJson(worldState, GameStateContainer.class);
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

  public void initializeWorld(EntityWorld worldToCopy) {
    world.clear();
    worldToCopy.world.values().forEach((componentTable) -> {
      componentTable.table.forEach(this::addComponent);
    });
    nextEntity = worldToCopy.nextEntity;
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
  public <T extends Component> T getComponent(int entity, Class<T> component) {
    ComponentTable<T> table = (ComponentTable<T>) world.get(component);
    if (table == null) {
      return null;
    }
    return table.get(entity);
  }

  /**
   * removes component from entity
   *
   * @param entity
   * @param component
   */
  @Override
  public void remove(int entity, Class<?> component) {
    ComponentTable<?> table = world.get(component);
    if (table != null) {
      table.remove(entity);
    }
  }

  /**
   * removes components from entity
   *
   * @param entity
   * @param componentsToRemove
   */
  @Override
  public void remove(int entity, Class<?>... componentsToRemove) {
    for (Class<?> component : componentsToRemove) {
      remove(entity, component);
    }
  }

  @Override
  public void removeEntity(int entity) {
    world.values().forEach(components -> components.remove(entity));
  }

  @Override
  public List<Integer> list(Class<?> component) {
    ComponentTable<?> table = world.get(component);
    if (table == null) {
      return Collections.emptyList();
    }
    return table.list();
  }

  @Override
  public List<Integer> findEntitiesByComponentValue(Component component) {
    ComponentTable table = world.get(component.getClass());
    if (table == null) {
      return Collections.emptyList();
    }
    return table.findEntitiesByValue(component);
  }

  @Override
  public boolean hasEntity(int entity) {
    return world.values().stream().anyMatch(components -> components.hasEntity(entity));
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
      ComponentTable components =
          world.computeIfAbsent(component.getClass(), x -> new ComponentTable<>());
      components.set(entity, component);
    }
  }

  // for serialization
  public Map<Integer, List<Component>> getWorld() {
    Map<Integer, List<Component>> result = new LinkedHashMap<>();
    for (ComponentTable<?> table : world.values()) {
      for (Integer entity : table.list()) {
        result.computeIfAbsent(entity, x -> new ArrayList<>()).add(table.get(entity));
      }
    }
    return Collections.unmodifiableMap(result);
  }
}
