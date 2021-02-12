package com.destrostudios.grid.components.spells;

import com.destrostudios.grid.components.Component;
import com.destrostudios.grid.components.PlayerComponent;
import com.destrostudios.grid.gamestate.ComponentAdapter;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DamageComponent implements Component {
    private final int damage;

    @Override
    public String toMarshalString() {
        return DamageComponent.class.getSimpleName() + ComponentAdapter.CLASS_SEPERATOR + damage;
    }
}
