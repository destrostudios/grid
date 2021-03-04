package com.destrostudios.grid.components.spells.buffs;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
public class MovementPointBuffComponent extends BuffComponent {
    public MovementPointBuffComponent(int buffAmount, int buffDuration, boolean isSpellBuff) {
        super(buffAmount, buffDuration, isSpellBuff);
    }
}
