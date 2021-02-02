package com.destrostudios.grid.client.blocks;

import com.destroflyer.jme3.cubes.*;
import com.jme3.app.Application;

public class BlockAssets {

    public static BlockTerrainControl createNewBlockTerrain(Application application, Vector3Int chunksCount) {
        return new BlockTerrainControl(getSettings(application), chunksCount);
    }

    public static CubesSettings getSettings(Application application) {
        CubesSettings cubesSettings = new CubesSettings(application);
        cubesSettings.setBlockSize(3);
        cubesSettings.setChunkSizeX(16);
        cubesSettings.setChunkSizeY(16);
        cubesSettings.setChunkSizeZ(16);
        cubesSettings.setDefaultBlockMaterial("textures/blocks.png");
        return cubesSettings;
    }

    public static final Block BLOCK_GRASS = new Block(
        new BlockSkin(new BlockSkin_TextureLocation(0, 0), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 0), false),
        new BlockSkin(new BlockSkin_TextureLocation(3, 0), false)
    ) {

        @Override
        protected int getSkinIndex(BlockChunkControl chunk, Vector3Int location, Face face) {
            if( chunk.isBlockOnSurface(location)) {
                switch(face){
                    case Top:
                        return 0;

                    case Bottom:
                        return 1;
                }
                return 2;
            }
            return 1;
        }
    };
    public static final Block BLOCK_STONE_TILE = new Block(new BlockSkin(new BlockSkin_TextureLocation(10, 1), false));
    public static final Block BLOCK_WOOD = new Block(
        new BlockSkin(new BlockSkin_TextureLocation(5, 1), false),
        new BlockSkin(new BlockSkin_TextureLocation(5, 1), false),
        new BlockSkin(new BlockSkin_TextureLocation(4, 1), false),
        new BlockSkin(new BlockSkin_TextureLocation(4, 1), false),
        new BlockSkin(new BlockSkin_TextureLocation(4, 1), false),
        new BlockSkin(new BlockSkin_TextureLocation(4, 1), false)
    );
    public static final Block BLOCK_BOX = new Block(new BlockSkin(new BlockSkin_TextureLocation(4, 0), false));
    public static final Block BLOCK_PINE_BARK = new Block(
        new BlockSkin(new BlockSkin_TextureLocation(11, 1), false),
        new BlockSkin(new BlockSkin_TextureLocation(11, 1), false),
        new BlockSkin(new BlockSkin_TextureLocation(5, 7), false),
        new BlockSkin(new BlockSkin_TextureLocation(5, 7), false),
        new BlockSkin(new BlockSkin_TextureLocation(5, 7), false),
        new BlockSkin(new BlockSkin_TextureLocation(5, 7), false)
    );
    public static final Block BLOCK_LEAVES = new Block(new BlockSkin(new BlockSkin_TextureLocation(10, 2), false));
    public static final Block BLOCK_GOLD = new Block(new BlockSkin(new BlockSkin_TextureLocation(9, 6), false));
    public static final Block BLOCK_SNOW_EARTH = new Block(
        new BlockSkin(new BlockSkin_TextureLocation(2, 4), false),
        new BlockSkin(new BlockSkin_TextureLocation(2, 0), false),
        new BlockSkin(new BlockSkin_TextureLocation(4, 4), false)
    ) {

        @Override
        protected int getSkinIndex(BlockChunkControl chunk, Vector3Int location, Face face) {
            if (chunk.isBlockOnSurface(location)) {
                switch(face){
                    case Top:
                        return 0;

                    case Bottom:
                        return 1;
                }
                return 2;
            }
            return 1;
        }
    };
    public static final Block BLOCK_SAND = new Block(new BlockSkin(new BlockSkin_TextureLocation(2, 1), false));
    public static final Block BLOCK_CACTUS = new Block(
        new BlockSkin(new BlockSkin_TextureLocation(5, 4), false),
        new BlockSkin(new BlockSkin_TextureLocation(5, 4), false),
        new BlockSkin(new BlockSkin_TextureLocation(6, 4), false),
        new BlockSkin(new BlockSkin_TextureLocation(6, 4), false),
        new BlockSkin(new BlockSkin_TextureLocation(6, 4), false),
        new BlockSkin(new BlockSkin_TextureLocation(6, 4), false)
    );

    public static void registerBlocks() {
        BlockManager.register(BLOCK_GRASS);
        BlockManager.register(BLOCK_STONE_TILE);
        BlockManager.register(BLOCK_WOOD);
        BlockManager.register(BLOCK_BOX);
        BlockManager.register(BLOCK_PINE_BARK);
        BlockManager.register(BLOCK_LEAVES);
        BlockManager.register(BLOCK_GOLD);
        BlockManager.register(BLOCK_SNOW_EARTH);
        BlockManager.register(BLOCK_SAND);
        BlockManager.register(BLOCK_CACTUS);
    }
}
