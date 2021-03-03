package com.destrostudios.grid.components.spells.buffs;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AttackPointsBuffComponent extends BuffComponent {
    public AttackPointsBuffComponent(int buffAmount, int buffDuration, boolean isSpellBuff) {
        super(buffAmount, buffDuration, isSpellBuff);
    }
}
