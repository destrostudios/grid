package com.destrostudios.grid.actions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CastSpellAction implements Action {
    private int targetX;
    private int targetY;
    private String playerIdentifier;
    private int spell;
}
