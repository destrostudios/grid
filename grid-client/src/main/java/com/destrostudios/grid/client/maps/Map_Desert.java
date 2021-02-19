package com.destrostudios.grid.client.maps;

import com.destrostudios.grid.client.blocks.BlockAssets;

public class Map_Desert extends Map {

    public Map_Desert() {
        super(BlockAssets.BLOCK_SAND, new MapBlock(BlockAssets.BLOCK_SAND_TOP_GRID, BlockAssets.BLOCK_SAND_TOP_TARGET));
    }
}
