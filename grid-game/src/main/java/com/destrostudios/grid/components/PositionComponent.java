package com.destrostudios.grid.components;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PositionComponent implements Component{
    private final int x;
    private final int y;
}
