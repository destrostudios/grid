package com.destrostudios.grid.client.maps;

import com.destrostudios.grid.client.blocks.BlockAssets;

public class Map_Arctic extends Map {

    public Map_Arctic() {
        super(BlockAssets.BLOCK_SNOW, new MapBlock(BlockAssets.BLOCK_SNOW_TOP_GRID, BlockAssets.BLOCK_SNOW_TOP_TARGET));
    }
}
