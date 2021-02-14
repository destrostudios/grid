package com.destrostudios.grid.serialization.container;

import com.destrostudios.grid.components.Component;
import com.destrostudios.grid.serialization.container.ComponentsContainer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GameStateContainer implements ComponentsContainer {

    @Getter
    @Setter
    private Map<Integer, List<Component>> components;

}
