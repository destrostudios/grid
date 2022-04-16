package com.destrostudios.grid.client.models.modifiers;

import com.destrostudios.grid.client.blocks.BlockAssets;
import com.destrostudios.grid.client.models.ModelModifier;
import com.destrostudios.grid.client.models.RegisteredModel;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;

public class ModelModifier_CursedGround extends ModelModifier {

    @Override
    public void modify(RegisteredModel registeredModel, AssetManager assetManager) {
        Geometry geometry = new Geometry(null, new Quad(1, 1));
        Material material = new Material(assetManager, "models/cursed_ground/resources/material.j3md");
        Texture texture = assetManager.loadTexture("models/cursed_ground/resources/diffuse.jpg");
        texture.setWrap(Texture.WrapMode.Repeat);
        material.setTexture("ColorMap", texture);
        geometry.setMaterial(material);
        geometry.move((BlockAssets.BLOCK_SIZE / -2f), 0.01f, (BlockAssets.BLOCK_SIZE / 2f));
        geometry.rotate(-1 * FastMath.HALF_PI, 0, 0);
        geometry.scale(BlockAssets.BLOCK_SIZE);
        registeredModel.getNode().attachChild(geometry);
    }
}
