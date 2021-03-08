package com.destrostudios.grid.client.blocks;

import com.destroflyer.jme3.cubes.Block;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GridBlock {
    private Block blockGrid;
    private Block blockValid;
    private Block blockInvalid;
    private Block blockImpacted;
}
