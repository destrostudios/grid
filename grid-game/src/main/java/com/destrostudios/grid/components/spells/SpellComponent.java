package com.destrostudios.grid.components.spells;

import com.destrostudios.grid.components.Component;
import com.destrostudios.grid.components.PlayerComponent;
import com.destrostudios.grid.gamestate.ComponentAdapter;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SpellComponent implements Component {
    private final int spell;

    @Override
    public String toMarshalString() {
        return SpellComponent.class.getSimpleName() + ComponentAdapter.CLASS_SEPERATOR + spell;
    }
}
