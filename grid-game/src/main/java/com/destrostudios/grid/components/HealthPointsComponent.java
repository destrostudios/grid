package com.destrostudios.grid.components;

import com.destrostudios.grid.game.gamestate.ComponentAdapter;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HealthPointsComponent implements Component {
    private final int health;

    @Override
    public String toMarshalString() {
        return HealthPointsComponent.class.getSimpleName() + ComponentAdapter.CLASS_SEPERATOR + health;
    }
}
