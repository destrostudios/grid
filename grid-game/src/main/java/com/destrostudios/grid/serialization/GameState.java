package com.destrostudios.grid.serialization;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

public class GameState {

    @Getter
    @Setter
    private Map<Integer, ComponentsWrapper> world;

}
