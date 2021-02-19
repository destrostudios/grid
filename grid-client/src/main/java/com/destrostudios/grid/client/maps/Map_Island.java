package com.destrostudios.grid.client.maps;

import com.destrostudios.grid.client.blocks.BlockAssets;

public class Map_Island extends Map {

    public Map_Island() {
        super(null, new MapBlock(BlockAssets.BLOCK_GRASS_TOP_GRID, BlockAssets.BLOCK_GRASS_TOP_TARGET));
    }
}
