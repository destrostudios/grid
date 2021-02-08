package com.destrostudios.grid.client.appstates;

import com.destroflyer.jme3.cubes.BlockNavigator;
import com.destroflyer.jme3.cubes.BlockTerrainControl;
import com.destroflyer.jme3.cubes.Vector3Int;
import com.destrostudios.grid.client.blocks.BlockAssets;
import com.destrostudios.grid.client.gameproxy.GameProxy;
import com.destrostudios.grid.client.models.ModelObject;
import com.destrostudios.grid.components.PlayerComponent;
import com.destrostudios.grid.components.PositionComponent;
import com.destrostudios.grid.components.RoundComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.update.eventbus.ComponentUpdateEvent;
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

import java.util.HashMap;
import java.util.Optional;

public class GameAppState extends BaseAppState implements ActionListener {

    private final GameProxy gameProxy;
    private Node blockTerrainNode;
    private BlockTerrainControl blockTerrainControl;
    private HashMap<Integer, ModelObject> playerModels = new HashMap<>();

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

        gameProxy.addListener(new PositionUpdateListener());

        udpateVisuals();
    }

    @Override
    public void update(float tpf) {
        if (gameProxy.update()) {
            udpateVisuals();
        }
        super.update(tpf);
    }

    private void udpateVisuals() {
        EntityWorld entityWorld = gameProxy.getGame().getWorld();
        for (int playerEntity : entityWorld.list(PlayerComponent.class)) {
            ModelObject modelObject = playerModels.computeIfAbsent(playerEntity, pe -> {
                ModelObject newModelObject = null;
                switch ((int) (Math.random() * 8)) {
                    case 0: newModelObject = createCharacterModel("aland",  "idle", 11.267f); break;
                    case 1: newModelObject = createCharacterModel("alice", "idle1", 1.867f); break;
                    case 2: newModelObject = createCharacterModel("dosaz", "idle", 7.417f); break;
                    case 3: newModelObject = createCharacterModel("dwarf_warrior", "idle1", 7.875f); break;
                    case 4: newModelObject = createCharacterModel("elven_archer", "idle1", 5.1f); break;
                    case 5: newModelObject = createCharacterModel("garmon", "idle2", 10); break;
                    case 6: newModelObject = createCharacterModel("scarlet", "idle", 2); break;
                    case 7: newModelObject = createCharacterModel("tristan", "idle1", 7.567f); break;
                }
                mainApplication.getRootNode().attachChild(newModelObject);
                return newModelObject;
            });
            PositionComponent positionComponent = entityWorld.getComponent(playerEntity, PositionComponent.class).get();
            modelObject.setLocalTranslation((positionComponent.getX() + 0.5f) * 3, 3, (positionComponent.getY() + 0.5f) * 3);
        }
        int activePlayerEntity = entityWorld.list(RoundComponent.class).get(0);
        String activePlayerName = entityWorld.getComponent(activePlayerEntity, PlayerComponent.class).get().getName();
        GuiAppState guiAppState = getAppState(GuiAppState.class);
        guiAppState.setCurrentPlayer(activePlayerName);
        guiAppState.setMP(99);
        guiAppState.setAP(42);
    }

    private ModelObject createCharacterModel(String name, String idleAnimationName, float idleAnimationLoopDuration) {
        ModelObject modelObject = new ModelObject(mainApplication.getAssetManager(), "models/" + name + "/skin_default.xml");
        modelObject.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        modelObject.playAnimation(idleAnimationName, idleAnimationLoopDuration);
        return modelObject;
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
        Integer playerEntity = gameProxy.getPlayerEntity();
        if (buttonIndex == 0) {
            gameProxy.requestAction(new ComponentUpdateEvent<>(playerEntity, new RoundComponent()));
        }
        System.out.println("GUI button #" + buttonIndex + " pressed");
    }
}
