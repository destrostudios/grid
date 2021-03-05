package com.destrostudios.grid.client.blocks;

import com.destroflyer.jme3.cubes.*;
import com.jme3.app.Application;

public class BlockAssets {

    public static int BLOCK_SIZE = 3;

    public static BlockTerrainControl createNewBlockTerrain(Application application, int chunkSizeX, int chunkSizeZ, Vector3Int chunksCount) {
        return new BlockTerrainControl(getSettings(application, chunkSizeX, chunkSizeZ), chunksCount);
    }

    private static CubesSettings getSettings(Application application, int chunkSizeX, int chunkSizeZ) {
        CubesSettings cubesSettings = new CubesSettings(application);
        cubesSettings.setBlockSize(BLOCK_SIZE);
        cubesSettings.setChunkSizeX(chunkSizeX);
        cubesSettings.setChunkSizeY(16);
        cubesSettings.setChunkSizeZ(chunkSizeZ);
        cubesSettings.setDefaultBlockMaterial("textures/blocks.png");
        return cubesSettings;
    }

    public static final Block BLOCK_GRASS = new Block(
        new BlockSkin(new BlockSkin_TextureLocation(0, 0), false),
        new BlockSkin(new BlockSkin_TextureLocation(3, 0), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 0), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 0), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 0), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 0), false)
    );
    public static final Block BLOCK_GRASS_TOP_GRID = new Block(
        new BlockSkin(new BlockSkin_TextureLocation(1, 0), false),
        new BlockSkin(new BlockSkin_TextureLocation(3, 0), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 0), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 0), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 0), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 0), false)
    );
    public static final Block BLOCK_GRASS_TOP_TARGET = new Block(
        new BlockSkin(new BlockSkin_TextureLocation(15, 0), false),
        new BlockSkin(new BlockSkin_TextureLocation(3, 0), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 0), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 0), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 0), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 0), false)
    );
    public static final Block BLOCK_SAND = new Block(new BlockSkin(new BlockSkin_TextureLocation(0, 2), false));
    public static final Block BLOCK_SAND_TOP_GRID = new Block(
        new BlockSkin(new BlockSkin_TextureLocation(1, 2), false),
        new BlockSkin(new BlockSkin_TextureLocation(0, 2), false),
        new BlockSkin(new BlockSkin_TextureLocation(0, 2), false),
        new BlockSkin(new BlockSkin_TextureLocation(0, 2), false),
        new BlockSkin(new BlockSkin_TextureLocation(0, 2), false),
        new BlockSkin(new BlockSkin_TextureLocation(0, 2), false)
    );
    public static final Block BLOCK_SAND_TOP_TARGET = new Block(
        new BlockSkin(new BlockSkin_TextureLocation(15, 0), false),
        new BlockSkin(new BlockSkin_TextureLocation(0, 2), false),
        new BlockSkin(new BlockSkin_TextureLocation(0, 2), false),
        new BlockSkin(new BlockSkin_TextureLocation(0, 2), false),
        new BlockSkin(new BlockSkin_TextureLocation(0, 2), false),
        new BlockSkin(new BlockSkin_TextureLocation(0, 2), false)
    );
    public static final Block BLOCK_SNOW = new Block(
        new BlockSkin(new BlockSkin_TextureLocation(0, 1), false),
        new BlockSkin(new BlockSkin_TextureLocation(3, 1), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 1), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 1), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 1), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 1), false)
    );
    public static final Block BLOCK_SNOW_TOP_GRID = new Block(
        new BlockSkin(new BlockSkin_TextureLocation(1, 1), false),
        new BlockSkin(new BlockSkin_TextureLocation(3, 1), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 1), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 1), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 1), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 1), false)
    );
    public static final Block BLOCK_SNOW_TOP_TARGET = new Block(
        new BlockSkin(new BlockSkin_TextureLocation(15, 0), false),
        new BlockSkin(new BlockSkin_TextureLocation(3, 1), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 1), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 1), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 1), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 1), false)
    );

    public static void registerBlocks() {
        BlockManager.register(BLOCK_GRASS);
        BlockManager.register(BLOCK_GRASS_TOP_GRID);
        BlockManager.register(BLOCK_GRASS_TOP_TARGET);
        BlockManager.register(BLOCK_SAND);
        BlockManager.register(BLOCK_SAND_TOP_GRID);
        BlockManager.register(BLOCK_SAND_TOP_TARGET);
        BlockManager.register(BLOCK_SNOW);
        BlockManager.register(BLOCK_SNOW_TOP_GRID);
        BlockManager.register(BLOCK_SNOW_TOP_TARGET);
    }
}
