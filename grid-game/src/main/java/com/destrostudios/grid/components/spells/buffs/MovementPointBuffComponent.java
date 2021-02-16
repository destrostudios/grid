package com.destrostudios.grid.components.spells.buffs;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MovementPointBuffComponent extends BuffComponent {
    public MovementPointBuffComponent(int buffAmount, int buffDuration) {
        super(buffAmount, buffDuration);
    }
}
