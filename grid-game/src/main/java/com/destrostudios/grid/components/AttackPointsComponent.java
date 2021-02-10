package com.destrostudios.grid.components;

import com.destrostudios.grid.gamestate.ComponentAdapter;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AttackPointsComponent implements Component {
    private final int attackPoints;

    @Override
    public String toMarshalString() {
        return AttackPointsComponent.class.getSimpleName() + ComponentAdapter.CLASS_SEPERATOR + attackPoints;
    }
}
