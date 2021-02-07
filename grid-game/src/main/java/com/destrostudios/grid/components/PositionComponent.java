package com.destrostudios.grid.components;

import com.destrostudios.grid.game.gamestate.ComponentAdapter;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PositionComponent implements Component {
    private final int x;
    private final int y;

    @Override
    public String toMarshalString() {
        return PositionComponent.class.getSimpleName() + ComponentAdapter.CLASS_SEPERATOR + x + ComponentAdapter.VALUE_SEPERATOR + y;
    }
}
