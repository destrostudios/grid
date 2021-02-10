package com.destrostudios.grid.components;

import com.destrostudios.grid.gamestate.ComponentAdapter;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public class PositionComponent implements Component {
    private final int x;
    private final int y;

    @Override
    public String toMarshalString() {
        return PositionComponent.class.getSimpleName() + ComponentAdapter.CLASS_SEPERATOR + x + ComponentAdapter.VALUE_SEPERATOR + y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PositionComponent that = (PositionComponent) o;
        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
