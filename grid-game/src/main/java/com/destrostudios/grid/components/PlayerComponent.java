package com.destrostudios.grid.components;

import com.destrostudios.grid.game.gamestate.ComponentAdapter;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PlayerComponent implements Component {
    private final String name;

    @Override
    public String toMarshalString() {
        return PlayerComponent.class.getSimpleName()+ ComponentAdapter.CLASS_SEPERATOR + name;
    }
}

