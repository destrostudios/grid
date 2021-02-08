package com.destrostudios.grid.client.appstates;

import com.destroflyer.jme3.cubes.BlockNavigator;
import com.destroflyer.jme3.cubes.BlockTerrainControl;
import com.destroflyer.jme3.cubes.Vector3Int;
import com.destrostudios.grid.client.GameProxy;
import com.destrostudios.grid.client.blocks.BlockAssets;
import com.destrostudios.grid.client.models.ModelObject;
import com.destrostudios.grid.components.PlayerComponent;
import com.destrostudios.grid.components.PositionComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.update.ComponentUpdateEvent;
import com.destrostudios.grid.update.listener.PositionUpdateListener;
import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.collision.CollisionResults;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.util.SkyFactory;
import java.util.Optional;

public class GameAppState extends BaseAppState implements ActionListener {

    private final GameProxy gameProxy;
    private Node blockTerrainNode;
    private BlockTerrainControl blockTerrainControl;
    private ModelObject modelObject;

    public GameAppState(GameProxy gameProxy) {
        this.gameProxy = gameProxy;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application application) {
        super.initialize(stateManager, application);
        mainApplication.getRootNode().attachChild(SkyFactory.createSky(mainApplication.getAssetManager(), "textures/sky.jpg", true));

        blockTerrainNode = new Node();
        blockTerrainNode.setShadowMode(RenderQueue.ShadowMode.Receive);
        blockTerrainControl = BlockAssets.createNewBlockTerrain(mainApplication, new Vector3Int(1, 1, 1));
        blockTerrainControl.setBlockArea(new Vector3Int(), new Vector3Int(16, 1, 16), BlockAssets.BLOCK_GRASS);
        blockTerrainNode.addControl(blockTerrainControl);
        mainApplication.getRootNode().attachChild(blockTerrainNode);

        Camera camera = mainApplication.getCamera();
        camera.setLocation(new Vector3f(22.621658f, 25.577982f, 75.90648f));
        camera.setRotation(new Quaternion(-8.6209044E-4f, 0.9749797f, -0.22225966f, -0.003803968f));

        mainApplication.getInputManager().addMapping("key_w", new KeyTrigger(KeyInput.KEY_W));
        mainApplication.getInputManager().addMapping("key_a", new KeyTrigger(KeyInput.KEY_A));
        mainApplication.getInputManager().addMapping("key_s", new KeyTrigger(KeyInput.KEY_S));
        mainApplication.getInputManager().addMapping("key_d", new KeyTrigger(KeyInput.KEY_D));
        mainApplication.getInputManager().addMapping("mouse_left", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        mainApplication.getInputManager().addMapping("mouse_right", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        mainApplication.getInputManager().addListener(this, "key_w", "key_a", "key_s", "key_d", "mouse_left", "mouse_right");

        gameProxy.addListener(new PositionUpdateListener(gameProxy.getGame().getWorld()));

        modelObject = new ModelObject(mainApplication.getAssetManager(), "models/aland/skin_default.xml");
        modelObject.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        modelObject.playAnimation("idle", 11.267f);
        mainApplication.getRootNode().attachChild(modelObject);

        addDemoModel("aland", 1, 13, "idle", 11.267f);
        addDemoModel("alice", 2, 6, "idle1", 1.867f);
        addDemoModel("dosaz", 5, 1, "idle", 7.417f);
        addDemoModel("dwarf_warrior", 7, 7, "idle1", 7.875f);
        addDemoModel("elven_archer", 10, 2, "idle1", 5.1f);
        addDemoModel("garmon", 10, 12, "idle2", 10);
        addDemoModel("scarlet", 13, 5, "idle", 2);
        addDemoModel("tristan", 14, 9, "idle1", 7.567f);

        udpateVisuals();
    }

    private void addDemoModel(String name, int tileX, int tileY, String idleAnimationName, float idleAnimationLoopDuration) {
        ModelObject modelObject = new ModelObject(mainApplication.getAssetManager(), "models/" + name + "/skin_default.xml");
        modelObject.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        modelObject.setLocalTranslation((tileX + 0.5f) * 3, 3, (tileY + 0.5f) * 3);
        modelObject.playAnimation(idleAnimationName, idleAnimationLoopDuration);
        mainApplication.getRootNode().attachChild(modelObject);
    }

    @Override
    public void update(float tpf) {
        if (gameProxy.update()) {
            udpateVisuals();
        }
        super.update(tpf);
    }

    private void udpateVisuals() {
        Integer playerEntity = gameProxy.getPlayerEntity();
        if (playerEntity == null) {
            // spectating only
            return;
        }

        EntityWorld entityWorld = gameProxy.getGame().getWorld();

        Optional<PositionComponent> positionComponentOptional = entityWorld.getComponent(playerEntity, PositionComponent.class);
        if (positionComponentOptional.isPresent()) {
            PositionComponent positionComponent = positionComponentOptional.get();
            modelObject.setLocalTranslation((positionComponent.getX() + 0.5f) * 3, 3, (positionComponent.getY() + 0.5f) * 3);
        }

        String playerName = entityWorld.getComponent(playerEntity, PlayerComponent.class).get().getName();
        GuiAppState guiAppState = getAppState(GuiAppState.class);
        guiAppState.setCurrentPlayer(playerName);
        guiAppState.setMP(99);
        guiAppState.setAP(42);
    }

    @Override
    public void cleanup() {
        super.cleanup();
        mainApplication.getRootNode().detachChild(blockTerrainNode);
        mainApplication.getInputManager().removeListener(this);
    }

    @Override
    public void onAction(String actionName, boolean isPressed, float tpf) {
        Integer playerEntity = gameProxy.getPlayerEntity();
        if (playerEntity == null) {
            // spectating only
            return;
        }
        Optional<PositionComponent> componentOpt = gameProxy.getGame().getWorld().getComponent(playerEntity, PositionComponent.class);
        if (componentOpt.isPresent() && isPressed) {
            PositionComponent positionComponent = componentOpt.get();
            switch (actionName) {
                case "key_w":
                    gameProxy.requestAction(new ComponentUpdateEvent<>(playerEntity, new PositionComponent(positionComponent.getX(), positionComponent.getY() - 1)));
                    break;
                case "key_a":
                    gameProxy.requestAction(new ComponentUpdateEvent<>(playerEntity, new PositionComponent(positionComponent.getX() - 1, positionComponent.getY())));
                    break;
                case "key_s":
                    gameProxy.requestAction(new ComponentUpdateEvent<>(playerEntity, new PositionComponent(positionComponent.getX(), positionComponent.getY() + 1)));
                    break;
                case "key_d":
                    gameProxy.requestAction(new ComponentUpdateEvent<>(playerEntity, new PositionComponent(positionComponent.getX() + 1, positionComponent.getY())));
                    break;
                case "mouse_left":
                case "mouse_right":
                    Vector3Int clickedPosition = getHoveredPosition();
                    if (clickedPosition != null) {
                        gameProxy.requestAction(new ComponentUpdateEvent<>(playerEntity, new PositionComponent(clickedPosition.getX(), clickedPosition.getZ())));
                    }
                    break;
            }
        }
    }

    private Vector3Int getHoveredPosition() {
        CollisionResults results = mainApplication.getRayCastingResults_Cursor(blockTerrainNode);
        if (results.size() > 0) {
            Vector3f collisionContactPoint = results.getClosestCollision().getContactPoint();
            return BlockNavigator.getPointedBlockLocation(blockTerrainControl, collisionContactPoint, false);
        }
        return null;
    }

    public void onButtonClicked(int buttonIndex) {
        System.out.println("GUI button #" + buttonIndex + " pressed");
    }
}
