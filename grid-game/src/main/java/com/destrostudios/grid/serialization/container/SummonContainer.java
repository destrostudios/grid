package com.destrostudios.grid.serialization.container;

import com.destrostudios.grid.components.Component;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SummonContainer implements ComponentsContainer {
  private Map<Integer, List<Component>> spells = new LinkedHashMap<>();
  private List<Component> properties = new LinkedList<>();

  @Override
  public Map<Integer, List<Component>> getComponents() {
    return spells;
  }
}
