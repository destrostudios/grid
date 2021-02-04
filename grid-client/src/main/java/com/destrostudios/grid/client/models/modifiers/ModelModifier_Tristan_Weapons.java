package com.destrostudios.grid.client.models.modifiers;

import com.destrostudios.grid.client.JMonkeyUtil;
import com.destrostudios.grid.client.models.ModelModifier;
import com.destrostudios.grid.client.models.ModelSkin;
import com.destrostudios.grid.client.models.RegisteredModel;
import com.jme3.asset.AssetManager;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

public class ModelModifier_Tristan_Weapons extends ModelModifier {

    public static final String NODE_NAME_SWORD = "tristanSword";

    @Override
    public void modify(RegisteredModel registeredModel, AssetManager assetManager) {
        // Shield
        Node leftPalmNode = registeredModel.requestBoneAttachmentsNode("RigLPalm");
        Node shield = ModelSkin.get("models/tristan_shield/skin.xml").load(assetManager);
        shield.setLocalTranslation(8, 3, 2);
        shield.rotate(new Quaternion().fromAngleAxis(FastMath.PI, Vector3f.UNIT_Y));
        shield.rotate(new Quaternion().fromAngleAxis(0.3f, Vector3f.UNIT_Z));
        shield.setLocalScale(100);
        leftPalmNode.attachChild(shield);
        // Sword
        Node rightPalmNode = registeredModel.requestBoneAttachmentsNode("RigRPalm");
        Node sword = ModelSkin.get("models/tristan_sword/skin.xml").load(assetManager);
        sword.setName(NODE_NAME_SWORD);
        sword.setLocalTranslation(8, -8, 2);
        JMonkeyUtil.lookAtDirection(sword, new Vector3f(0, 0, 1));
        sword.setLocalScale(100);
        rightPalmNode.attachChild(sword);
    }
}
