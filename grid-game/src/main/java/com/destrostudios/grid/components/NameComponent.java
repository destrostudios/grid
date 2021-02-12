package com.destrostudios.grid.components;

import com.destrostudios.grid.gamestate.ComponentAdapter;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NameComponent implements Component {
    private final String name;

    @Override
    public String toMarshalString() {
        return NameComponent.class.getSimpleName() + ComponentAdapter.CLASS_SEPERATOR + name;
    }
}
