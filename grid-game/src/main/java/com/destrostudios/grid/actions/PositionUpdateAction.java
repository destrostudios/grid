package com.destrostudios.grid.actions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PositionUpdateAction implements Action {
    private int newX;
    private int newY;

    private String playerIdentifier;

    @Override
    public String getPlayerIdentifier() {
        return playerIdentifier;
    }
}
