package com.destrostudios.grid.components.spells.poison;

import com.destrostudios.grid.components.Component;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;


@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class MovementPointsPerTurnComponent implements Component {
    private int poisonMinValue;
    private int poisonMaxValue;
    private int poisonDuration;
    private int sourceEntity;
}
