package com.destrostudios.grid.client.maps;

import com.destrostudios.grid.client.blocks.BlockAssets;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

public class Map_Desert extends Map {

    public Map_Desert() {
        super(
            BlockAssets.BLOCK_SAND,
            new MapBlock(BlockAssets.BLOCK_SAND_TOP_GRID, BlockAssets.BLOCK_SAND_TOP_TARGET),
            new Vector3f(23.15413f, 40.838593f, 66.12133f),
            new Quaternion(-8.0925995E-4f, 0.9084759f, -0.4179328f, -0.0017719142f)
        );
    }
}
