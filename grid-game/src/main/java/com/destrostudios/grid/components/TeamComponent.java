package com.destrostudios.grid.components;

import com.destrostudios.grid.game.gamestate.ComponentAdapter;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class TeamComponent implements Component {
    private final int team;

    @Override
    public String toMarshalString() {
        return TeamComponent.class.getSimpleName() + ComponentAdapter.CLASS_SEPERATOR + team;
    }
}
