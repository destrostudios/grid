package com.destrostudios.grid.client.models.modifiers;

import com.destrostudios.grid.client.models.ModelModifier;
import com.destrostudios.grid.client.models.ModelSkin;
import com.destrostudios.grid.client.models.RegisteredModel;
import com.jme3.asset.AssetManager;
import com.jme3.math.FastMath;
import com.jme3.scene.Node;

public class ModelModifier_Scarlet_Weapons extends ModelModifier {

    @Override
    public void modify(RegisteredModel registeredModel, AssetManager assetManager) {
        addKunai(registeredModel, assetManager, "RigLPalm");
        addKunai(registeredModel, assetManager, "RigRPalm");
    }

    private void addKunai(RegisteredModel registeredModel, AssetManager assetManager, String boneName) {
        Node palmNode = registeredModel.requestBoneAttachmentsNode(boneName);
        Node kunai = ModelSkin.get("models/scarlet_kunai/skin.xml").load(assetManager);
        kunai.setLocalTranslation(-3, -8, -8);
        kunai.rotate(FastMath.PI, 0, FastMath.HALF_PI);
        kunai.setLocalScale(150);
        palmNode.attachChild(kunai);
    }
}
