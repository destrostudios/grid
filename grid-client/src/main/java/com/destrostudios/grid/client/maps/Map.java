package com.destrostudios.grid.client.maps;

import com.destroflyer.jme3.cubes.Block;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public abstract class Map {
    private Block environmentBlock;
    private MapBlock terrainBlock;
}
