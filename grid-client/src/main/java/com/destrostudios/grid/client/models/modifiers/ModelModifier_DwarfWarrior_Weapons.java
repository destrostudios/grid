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

public class ModelModifier_DwarfWarrior_Weapons extends ModelModifier {

    public static final String NODE_NAME_HAMMER = "dwarfWarriorHammer";

    @Override
    public void modify(RegisteredModel registeredModel, AssetManager assetManager) {
        // Shield
        Node spineNode = registeredModel.requestBoneAttachmentsNode("RigSpine3");
        Node shield = ModelSkin.get("models/dwarf_warrior_shield/skin.xml").load(assetManager);
        shield.setLocalTranslation(17, -6, 0);
        shield.rotate(new Quaternion().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_Z));
        shield.rotate(new Quaternion().fromAngleAxis(-1.5f, Vector3f.UNIT_Y));
        shield.setLocalScale(100);
        spineNode.attachChild(shield);
        // Axe
        Node leftPalmNode = registeredModel.requestBoneAttachmentsNode("RigLPalm");
        Node axe = ModelSkin.get("models/dwarf_warrior_axe/skin.xml").load(assetManager);
        axe.setLocalTranslation(4, -11, 2);
        JMonkeyUtil.lookAtDirection(axe, new Vector3f(0, 0, -1));
        axe.setLocalScale(100);
        leftPalmNode.attachChild(axe);
        // Hammer
        Node rightPalmNode = registeredModel.requestBoneAttachmentsNode("RigRPalm");
        Node hammer = ModelSkin.get("models/dwarf_warrior_hammer/skin.xml").load(assetManager);
        hammer.setName(NODE_NAME_HAMMER);
        hammer.setLocalTranslation(4, -11, 2);
        JMonkeyUtil.lookAtDirection(hammer, new Vector3f(0, 0, 1));
        hammer.setLocalScale(100);
        rightPalmNode.attachChild(hammer);
    }
}
