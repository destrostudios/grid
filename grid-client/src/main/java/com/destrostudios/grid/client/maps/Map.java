package com.destrostudios.grid.client.maps;

import com.destroflyer.jme3.cubes.Block;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public abstract class Map {
    private Block environmentBlock;
    private Vector3f cameraPosition;
    private Quaternion cameraRotation;
}
