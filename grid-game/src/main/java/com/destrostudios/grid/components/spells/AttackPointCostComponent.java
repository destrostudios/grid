package com.destrostudios.grid.components.spells;

import com.destrostudios.grid.components.Component;
import com.destrostudios.grid.components.PlayerComponent;
import com.destrostudios.grid.gamestate.ComponentAdapter;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AttackPointCostComponent implements Component {
    private final int attackPointCosts;

    @Override
    public String toMarshalString() {
        return AttackPointCostComponent.class.getSimpleName() + ComponentAdapter.CLASS_SEPERATOR + attackPointCosts;
    }
}
