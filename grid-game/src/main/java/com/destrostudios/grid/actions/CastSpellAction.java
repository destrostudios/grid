package com.destrostudios.grid.actions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CastSpellAction implements Action{
    private final int targetEntity;
    private final String playerIdentifier;
    private final int spell;
}
