package com.destrostudios.grid.components.spells.buffs;

import com.destrostudios.grid.components.properties.BuffsComponent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DamageBuffComponent extends BuffComponent {
    public DamageBuffComponent(int buffAmount, int buffDuration, boolean isSpellBuff) {
        super(buffAmount, buffDuration, isSpellBuff);
    }
}
