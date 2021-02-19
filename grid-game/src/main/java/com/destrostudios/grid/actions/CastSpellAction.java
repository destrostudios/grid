package com.destrostudios.grid.actions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CastSpellAction implements Action {
    private int targetX;
    private int targetY;
    private String playerIdentifier;
    private int spell;
}
