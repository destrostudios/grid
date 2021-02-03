package com.destrostudios.grid.client.appstates;

import com.destroflyer.jme3.cubes.BlockTerrainControl;
import com.destroflyer.jme3.cubes.Vector3Int;
import com.destrostudios.grid.client.blocks.BlockAssets;
import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
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

        Geometry box = createBox();
        box.setLocalTranslation(1.5f, 4, 1.5f);
        mainApplication.getRootNode().attachChild(box);
    }

    @Override
    public void cleanup(){
        super.cleanup();
        mainApplication.getRootNode().detachChild(blockTerrainNode);
        mainApplication.getInputManager().removeListener(this);
    }

    @Override
    public void onAction(String actionName, boolean isPressed, float tpf) {
        System.out.println(actionName + "\t" + isPressed);
    }

    private Geometry createBox() {
        Geometry geometry = new Geometry(null, new Box(1, 1, 1));
        Material material = new Material(mainApplication.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        material.setBoolean("UseMaterialColors", true);
        material.setColor("Diffuse", ColorRGBA.Red);
        geometry.setMaterial(material);
        return geometry;
    }
}
