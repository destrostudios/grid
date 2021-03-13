package com.destrostudios.grid.client.blocks;

public class GridBlocks {

    private static GridBlock GRASS = new GridBlock(BlockAssets.BLOCK_GRASS_GRID, BlockAssets.BLOCK_GRASS_VALID, BlockAssets.BLOCK_GRASS_INVALID, BlockAssets.BLOCK_GRASS_IMPACTED);
    private static GridBlock SAND = new GridBlock(BlockAssets.BLOCK_SAND_GRID, BlockAssets.BLOCK_SAND_VALID, BlockAssets.BLOCK_SAND_INVALID, BlockAssets.BLOCK_SAND_IMPACTED);
    private static GridBlock SNOW = new GridBlock(BlockAssets.BLOCK_SNOW_GRID, BlockAssets.BLOCK_SNOW_VALID, BlockAssets.BLOCK_SNOW_INVALID, BlockAssets.BLOCK_SNOW_IMPACTED);
    private static GridBlock GLASS = new GridBlock(BlockAssets.BLOCK_GLASS_GRID, BlockAssets.BLOCK_GLASS_VALID, BlockAssets.BLOCK_GLASS_INVALID, BlockAssets.BLOCK_GLASS_IMPACTED);

    public static GridBlock get(String name) {
        switch (name) {
            case "grass": return GRASS;
            case "snow": return SNOW;
            case "sand": return SAND;
            case "glass": return GLASS;
        }
        throw new IllegalArgumentException(name);
    }
}
