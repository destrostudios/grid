package com.destrostudios.grid.entities;

import com.destrostudios.grid.components.Component;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ComponentTable<T extends Component> {
    private final Map<Integer, T> table = new LinkedHashMap<>();
    private transient Map<T, Set<Integer>> index;

    public T get(int entity) {
        return table.get(entity);
    }

    public void add(int entity, T value) {
        table.put(entity, value);
        if (index != null) {
            index.computeIfAbsent(value, x -> new HashSet<>()).add(entity);
        }
    }

    public void remove(int entity) {
        T removed = table.remove(entity);
        if (index != null && removed != null) {
            Set<Integer> entities = index.get(removed);
            entities.remove(entity);
            if (entities.isEmpty()) {
                index.remove(removed);
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
                index.computeIfAbsent(entry.getValue(), x -> new HashSet<>()).add(entry.getKey());
            }
        }
        Set<Integer> rawEntities = index.get(component);
        if (rawEntities == null) {
            return Collections.emptyList();
        }
        ArrayList<Integer> entities = new ArrayList<>(rawEntities);
        Collections.sort(entities);// sort to make result deterministic
        return entities;
    }

    public boolean hasEntity(int entity) {
        return table.containsKey(entity);
    }
}
