package com.destrostudios.grid.serialization;

import com.destrostudios.grid.components.Component;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class GridMap {
    private Map<Integer, List<Component>> map = new LinkedHashMap<>();
}
