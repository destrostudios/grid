package com.destrostudios.grid.entities;

import com.destrostudios.grid.components.Component;

import java.util.*;

public class ComponentTable<T extends Component> {
  private final Map<Integer, T> table = new TreeMap<>();
  private transient Map<T, Set<Integer>> index;

  public T get(int entity) {
    return table.get(entity);
  }

  public void set(int entity, T value) {
    T removed = table.put(entity, value);
    if (index != null) {
      removeFromIndex(entity, removed);
      index.computeIfAbsent(value, x -> new LinkedHashSet<>()).add(entity);
    }
  }

  public void remove(int entity) {
    T removed = table.remove(entity);
    if (index != null) {
      removeFromIndex(entity, removed);
    }
  }

  private void removeFromIndex(int entity, T value) {
    if (value != null) {
      Set<Integer> entities = index.get(value);
      entities.remove(entity);
      if (entities.isEmpty()) {
        index.remove(value);
      }
    }
  }

  public List<Integer> list() {
    return new ArrayList<>(table.keySet());
  }

  public List<Integer> findEntitiesByValue(T component) {
    if (index == null) {
      index = new HashMap<>();
      for (Map.Entry<Integer, T> entry : table.entrySet()) {
        index.computeIfAbsent(entry.getValue(), x -> new LinkedHashSet<>()).add(entry.getKey());
      }
    }
    return new ArrayList<>(index.getOrDefault(component, Collections.emptySet()));
  }

  public boolean hasEntity(int entity) {
    return table.containsKey(entity);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ComponentTable)) {
      return false;
    }

    ComponentTable<?> that = (ComponentTable<?>) o;
    return table.equals(that.table);
  }

  @Override
  public int hashCode() {
    return table.hashCode();
  }
}
