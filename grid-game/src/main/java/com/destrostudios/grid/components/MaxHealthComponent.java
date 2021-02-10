package com.destrostudios.grid.components;

import com.destrostudios.grid.gamestate.ComponentAdapter;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MaxHealthComponent implements Component {
    private final int maxHealth;

    @Override
    public String toMarshalString() {
        return MaxHealthComponent.class.getSimpleName() + ComponentAdapter.CLASS_SEPERATOR + maxHealth;
    }
}
