package com.destrostudios.grid.components.spells;

import com.destrostudios.grid.components.Component;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AttackPointCostComponent implements Component {
    private final int attackPointCosts;
}
