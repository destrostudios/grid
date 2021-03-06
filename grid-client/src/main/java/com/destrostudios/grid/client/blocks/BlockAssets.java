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
    public static final Block BLOCK_GRASS_GRID = new Block(
        new BlockSkin(new BlockSkin_TextureLocation(1, 0), false),
        new BlockSkin(new BlockSkin_TextureLocation(3, 0), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 0), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 0), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 0), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 0), false)
    );
    public static final Block BLOCK_GRASS_VALID = new Block(
        new BlockSkin(new BlockSkin_TextureLocation(13, 0), false),
        new BlockSkin(new BlockSkin_TextureLocation(3, 0), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 0), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 0), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 0), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 0), false)
    );
    public static final Block BLOCK_GRASS_INVALID = new Block(
        new BlockSkin(new BlockSkin_TextureLocation(14, 0), false),
        new BlockSkin(new BlockSkin_TextureLocation(3, 0), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 0), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 0), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 0), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 0), false)
    );
    public static final Block BLOCK_GRASS_IMPACTED = new Block(
        new BlockSkin(new BlockSkin_TextureLocation(15, 0), false),
        new BlockSkin(new BlockSkin_TextureLocation(3, 0), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 0), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 0), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 0), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 0), false)
    );
    public static final Block BLOCK_SAND = new Block(
        new BlockSkin(new BlockSkin_TextureLocation(0, 2), false)
    );
    public static final Block BLOCK_SAND_GRID = new Block(
        new BlockSkin(new BlockSkin_TextureLocation(1, 2), false),
        new BlockSkin(new BlockSkin_TextureLocation(0, 2), false),
        new BlockSkin(new BlockSkin_TextureLocation(0, 2), false),
        new BlockSkin(new BlockSkin_TextureLocation(0, 2), false),
        new BlockSkin(new BlockSkin_TextureLocation(0, 2), false),
        new BlockSkin(new BlockSkin_TextureLocation(0, 2), false)
    );
    public static final Block BLOCK_SAND_VALID = new Block(
        new BlockSkin(new BlockSkin_TextureLocation(13, 0), false),
        new BlockSkin(new BlockSkin_TextureLocation(0, 2), false),
        new BlockSkin(new BlockSkin_TextureLocation(0, 2), false),
        new BlockSkin(new BlockSkin_TextureLocation(0, 2), false),
        new BlockSkin(new BlockSkin_TextureLocation(0, 2), false),
        new BlockSkin(new BlockSkin_TextureLocation(0, 2), false)
    );
    public static final Block BLOCK_SAND_INVALID = new Block(
        new BlockSkin(new BlockSkin_TextureLocation(14, 0), false),
        new BlockSkin(new BlockSkin_TextureLocation(0, 2), false),
        new BlockSkin(new BlockSkin_TextureLocation(0, 2), false),
        new BlockSkin(new BlockSkin_TextureLocation(0, 2), false),
        new BlockSkin(new BlockSkin_TextureLocation(0, 2), false),
        new BlockSkin(new BlockSkin_TextureLocation(0, 2), false)
    );
    public static final Block BLOCK_SAND_IMPACTED = new Block(
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
    public static final Block BLOCK_SNOW_GRID = new Block(
        new BlockSkin(new BlockSkin_TextureLocation(1, 1), false),
        new BlockSkin(new BlockSkin_TextureLocation(3, 1), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 1), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 1), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 1), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 1), false)
    );
    public static final Block BLOCK_SNOW_VALID = new Block(
        new BlockSkin(new BlockSkin_TextureLocation(13, 0), false),
        new BlockSkin(new BlockSkin_TextureLocation(3, 1), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 1), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 1), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 1), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 1), false)
    );
    public static final Block BLOCK_SNOW_INVALID = new Block(
        new BlockSkin(new BlockSkin_TextureLocation(14, 0), false),
        new BlockSkin(new BlockSkin_TextureLocation(3, 1), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 1), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 1), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 1), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 1), false)
    );
    public static final Block BLOCK_SNOW_IMPACTED = new Block(
        new BlockSkin(new BlockSkin_TextureLocation(15, 0), false),
        new BlockSkin(new BlockSkin_TextureLocation(3, 1), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 1), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 1), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 1), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 1), false)
    );
    public static final Block BLOCK_GLASS = new Block(
        new BlockSkin(new BlockSkin_TextureLocation(0, 3), true)
    );
    public static final Block BLOCK_GLASS_GRID = new Block(
        new BlockSkin(new BlockSkin_TextureLocation(1, 3), true),
        new BlockSkin(new BlockSkin_TextureLocation(0, 3), true),
        new BlockSkin(new BlockSkin_TextureLocation(0, 3), true),
        new BlockSkin(new BlockSkin_TextureLocation(0, 3), true),
        new BlockSkin(new BlockSkin_TextureLocation(0, 3), true),
        new BlockSkin(new BlockSkin_TextureLocation(0, 3), true)
    );
    public static final Block BLOCK_GLASS_VALID = new Block(
        new BlockSkin(new BlockSkin_TextureLocation(13, 0), false),
        new BlockSkin(new BlockSkin_TextureLocation(0, 3), true),
        new BlockSkin(new BlockSkin_TextureLocation(0, 3), true),
        new BlockSkin(new BlockSkin_TextureLocation(0, 3), true),
        new BlockSkin(new BlockSkin_TextureLocation(0, 3), true),
        new BlockSkin(new BlockSkin_TextureLocation(0, 3), true)
    );
    public static final Block BLOCK_GLASS_INVALID = new Block(
        new BlockSkin(new BlockSkin_TextureLocation(14, 0), false),
        new BlockSkin(new BlockSkin_TextureLocation(0, 3), true),
        new BlockSkin(new BlockSkin_TextureLocation(0, 3), true),
        new BlockSkin(new BlockSkin_TextureLocation(0, 3), true),
        new BlockSkin(new BlockSkin_TextureLocation(0, 3), true),
        new BlockSkin(new BlockSkin_TextureLocation(0, 3), true)
    );
    public static final Block BLOCK_GLASS_IMPACTED = new Block(
        new BlockSkin(new BlockSkin_TextureLocation(15, 0), false),
        new BlockSkin(new BlockSkin_TextureLocation(0, 3), true),
        new BlockSkin(new BlockSkin_TextureLocation(0, 3), true),
        new BlockSkin(new BlockSkin_TextureLocation(0, 3), true),
        new BlockSkin(new BlockSkin_TextureLocation(0, 3), true),
        new BlockSkin(new BlockSkin_TextureLocation(0, 3), true)
    );

    public static void registerBlocks() {
        BlockManager.register(BLOCK_GRASS);
        BlockManager.register(BLOCK_GRASS_GRID);
        BlockManager.register(BLOCK_GRASS_VALID);
        BlockManager.register(BLOCK_GRASS_INVALID);
        BlockManager.register(BLOCK_GRASS_IMPACTED);
        BlockManager.register(BLOCK_SAND);
        BlockManager.register(BLOCK_SAND_GRID);
        BlockManager.register(BLOCK_SAND_VALID);
        BlockManager.register(BLOCK_SAND_INVALID);
        BlockManager.register(BLOCK_SAND_IMPACTED);
        BlockManager.register(BLOCK_SNOW);
        BlockManager.register(BLOCK_SNOW_GRID);
        BlockManager.register(BLOCK_SNOW_VALID);
        BlockManager.register(BLOCK_SNOW_INVALID);
        BlockManager.register(BLOCK_SNOW_IMPACTED);
        BlockManager.register(BLOCK_GLASS);
        BlockManager.register(BLOCK_GLASS_GRID);
        BlockManager.register(BLOCK_GLASS_VALID);
        BlockManager.register(BLOCK_GLASS_INVALID);
        BlockManager.register(BLOCK_GLASS_IMPACTED);
    }
}
