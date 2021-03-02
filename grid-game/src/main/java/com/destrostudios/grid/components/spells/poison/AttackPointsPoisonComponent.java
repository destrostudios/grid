package com.destrostudios.grid.components.spells.poison;

import com.destrostudios.grid.components.Component;
import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttackPointsPoisonComponent implements Component {
    private int poisonMinValue;
    private int poisonMaxValue;
    private int poisonDuration;
    private int sourceEntity;
}
