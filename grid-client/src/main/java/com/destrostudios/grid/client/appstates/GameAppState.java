package com.destrostudios.grid.client.appstates;

import com.destroflyer.jme3.cubes.BlockTerrainControl;
import com.destroflyer.jme3.cubes.Vector3Int;
import com.destrostudios.grid.client.blocks.BlockAssets;
import com.destrostudios.grid.client.models.MaterialFactory;
import com.destrostudios.grid.client.models.ModelObject;
import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.util.SkyFactory;

public class GameAppState extends BaseAppState implements ActionListener {

    private Node blockTerrainNode;

    @Override
    public void initialize(AppStateManager stateManager, Application application) {
        super.initialize(stateManager, application);
        mainApplication.getRootNode().attachChild(SkyFactory.createSky(mainApplication.getAssetManager(), "textures/sky.jpg", true));

        blockTerrainNode = new Node();
        blockTerrainNode.setShadowMode(RenderQueue.ShadowMode.Receive);
        BlockTerrainControl blockTerrainControl = BlockAssets.createNewBlockTerrain(mainApplication, new Vector3Int(1, 1, 1));
        blockTerrainControl.setBlockArea(new Vector3Int(), new Vector3Int(16, 1, 16), BlockAssets.BLOCK_GRASS);
        blockTerrainNode.addControl(blockTerrainControl);
        mainApplication.getRootNode().attachChild(blockTerrainNode);

        Camera camera = mainApplication.getCamera();
        camera.setLocation(new Vector3f(-17.826008f, 33.430782f, 52.07214f));
        camera.setRotation(new Quaternion(0.16455548f, 0.82591754f, -0.3061609f, 0.44390023f));

        mainApplication.getInputManager().addMapping("key_w", new KeyTrigger(KeyInput.KEY_W));
        mainApplication.getInputManager().addMapping("key_a", new KeyTrigger(KeyInput.KEY_A));
        mainApplication.getInputManager().addMapping("key_s", new KeyTrigger(KeyInput.KEY_S));
        mainApplication.getInputManager().addMapping("key_d", new KeyTrigger(KeyInput.KEY_D));
        mainApplication.getInputManager().addListener(this, "key_w", "key_a", "key_s", "key_d");

        Geometry box = new Geometry(null, new Box(1, 1, 1));
        box.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        box.setMaterial(MaterialFactory.generateLightingMaterial(mainApplication.getAssetManager(), ColorRGBA.Red));
        box.setLocalTranslation(1.5f, 4, 1.5f);
        mainApplication.getRootNode().attachChild(box);

        addDemoModel("aland", 1, 13, "idle", 11.267f);
        addDemoModel("alice", 2, 6, "idle1", 1.867f);
        addDemoModel("dosaz", 5, 1, "idle", 7.417f);
        addDemoModel("dwarf_warrior", 7, 7, "idle1", 7.875f);
        addDemoModel("elven_archer", 10, 2, "idle1", 5.1f);
        addDemoModel("garmon", 10, 12, "idle2", 10);
        addDemoModel("scarlet", 13, 5, "idle", 2);
        addDemoModel("tristan", 14, 9, "idle1", 7.567f);
    }

    @Override
    public void cleanup() {
        super.cleanup();
        mainApplication.getRootNode().detachChild(blockTerrainNode);
        mainApplication.getInputManager().removeListener(this);
    }

    @Override
    public void onAction(String actionName, boolean isPressed, float tpf) {
        System.out.println(actionName + "\t" + isPressed);
    }

    private void addDemoModel(String name, int tileX, int tileY, String idleAnimationName, float idleAnimationLoopDuration) {
        ModelObject modelObject = new ModelObject(mainApplication.getAssetManager(), "models/" + name + "/skin_default.xml");
        modelObject.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        modelObject.setLocalTranslation((tileX + 0.5f) * 3, 3, (tileY + 0.5f) * 3);
        modelObject.playAnimation(idleAnimationName, idleAnimationLoopDuration);
        mainApplication.getRootNode().attachChild(modelObject);
    }
}
