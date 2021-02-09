package com.destrostudios.grid.update.actions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PositionUpdateAction {
    private final int newX;
    private final int newY;
}
