package com.destrostudios.grid.components.spells.buffs;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class HealthPointBuffComponent extends BuffComponent {
    public HealthPointBuffComponent(int buffAmount, int buffDuration) {
        super(buffAmount, buffDuration);
    }
}
