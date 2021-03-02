package com.destrostudios.grid.components.spells.buffs;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MovementPointBuffComponent extends BuffComponent {
    public MovementPointBuffComponent(int buffAmount, int buffDuration) {
        super(buffAmount, buffDuration);
    }
}
