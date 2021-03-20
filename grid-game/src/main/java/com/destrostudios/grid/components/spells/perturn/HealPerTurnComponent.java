package com.destrostudios.grid.components.spells.perturn;

import com.destrostudios.grid.components.Component;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class HealPerTurnComponent implements Component {
    private int minValue;
    private int maxValue;
    private int duration;
}