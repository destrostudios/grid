package com.destrostudios.grid.components.spells.perturn;

import com.destrostudios.grid.components.Component;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;


@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class DamagePerTurnComponent implements Component {
    private int damageMinValue;
    private int damageMaxValue;
    private int duration;
    private int sourceEntity;
}
