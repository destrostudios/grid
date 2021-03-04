package com.destrostudios.grid.components.spells.buffs;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
public class HealBuffComponent extends BuffComponent {
    public HealBuffComponent(int buffAmount, int buffDuration, boolean isSpellBuff) {
        super(buffAmount, buffDuration, isSpellBuff);
    }
}
