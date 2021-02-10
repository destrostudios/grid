package com.destrostudios.grid.components;

import com.destrostudios.grid.gamestate.ComponentAdapter;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MovementPointsComponent implements Component {
    private final int movementPoints;

    @Override
    public String toMarshalString() {
        return MovementPointsComponent.class.getSimpleName() + ComponentAdapter.CLASS_SEPERATOR + movementPoints;
    }
}
