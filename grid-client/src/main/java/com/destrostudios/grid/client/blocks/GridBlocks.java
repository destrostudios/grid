package com.destrostudios.grid.client.blocks;

public class GridBlocks {

    private static GridBlock GRASS = new GridBlock(BlockAssets.BLOCK_GRASS_TOP_GRID, BlockAssets.BLOCK_GRASS_TOP_TARGET);
    private static GridBlock SAND = new GridBlock(BlockAssets.BLOCK_SAND_TOP_GRID, BlockAssets.BLOCK_SAND_TOP_TARGET);
    private static GridBlock SNOW = new GridBlock(BlockAssets.BLOCK_SNOW_TOP_GRID, BlockAssets.BLOCK_SNOW_TOP_TARGET);

    public static GridBlock get(String name) {
        switch (name) {
            case "grass": return GRASS;
            case "snow": return SNOW;
            case "sand": return SAND;
        }
        throw new IllegalArgumentException(name);
    }
}
