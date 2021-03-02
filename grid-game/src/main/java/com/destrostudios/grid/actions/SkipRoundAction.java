package com.destrostudios.grid.actions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkipRoundAction implements Action {
    private String playerIdentifier;

    @Override
    public String getPlayerIdentifier() {
        return playerIdentifier;
    }
}
