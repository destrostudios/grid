package com.destrostudios.grid.tools.mapeditor;

import com.destrostudios.grid.client.FileAssets;
import com.destrostudios.grid.client.JMonkeyUtil;
import com.destrostudios.grid.client.blocks.BlockAssets;

public class MapEditorMain {

    public static void main(String[] args) {
        FileAssets.readRootFile();
        BlockAssets.registerBlocks();
        JMonkeyUtil.disableLogger();
        new MapEditorApplication().start();
    }
}
