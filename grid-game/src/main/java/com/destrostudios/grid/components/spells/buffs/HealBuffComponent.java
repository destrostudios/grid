package com.destrostudios.grid.components.spells.buffs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HealBuffComponent extends BuffComponent {
    public HealBuffComponent(int buffAmount, int buffDuration, boolean isSpellBuff) {
        super(buffAmount, buffDuration, isSpellBuff);
    }
}
