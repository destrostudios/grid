package com.destrostudios.grid.client;

import com.destrostudios.grid.client.blocks.BlockAssets;

public class PositionUtil {

    public static final float CHARACTER_Y = BlockAssets.BLOCK_SIZE;

    public static float get3dCoordinate(int coordinate) {
        return (coordinate + 0.5f) * BlockAssets.BLOCK_SIZE;
    }
}
