package com.destrostudios.grid.components.spells.range;

import com.destrostudios.grid.components.Component;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class AffectedAreaComponent implements Component {
    SpellAreaShape shape;
    int minImpact;
    int maxImpact;


    public String toTooltipString() {
        return shape == SpellAreaShape.SINGLE
                ? "to a single field"
                : String.format("in a %s-%s %s shape", minImpact, maxImpact, shape.toTooltipString());
    }
}
