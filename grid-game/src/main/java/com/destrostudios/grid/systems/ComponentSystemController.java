package com.destrostudios.grid.systems;


import com.destrostudios.grid.entities.EntityWorld;

import java.util.LinkedHashSet;
import java.util.Set;

public class ComponentSystemController {
    private final Set<ComponentSystem> systems;

    public ComponentSystemController() {
        this.systems = new LinkedHashSet<>();
        initSystems();
    }

    private void initSystems() {
        this.systems.add(new MovementSystem());
    }

}
