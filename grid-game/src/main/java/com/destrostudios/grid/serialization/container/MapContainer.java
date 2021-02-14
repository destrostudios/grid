package com.destrostudios.grid.serialization.container;

import com.destrostudios.grid.components.Component;
import com.destrostudios.grid.serialization.container.ComponentsContainer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MapContainer implements ComponentsContainer {
    private Map<Integer, List<Component>> components = new LinkedHashMap<>();
}
