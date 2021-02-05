package com.destrostudios.grid.components;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class PlayerComponent implements Component {

    @Getter
    private final String name;
}

