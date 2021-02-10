package com.destrostudios.grid.client.appstates;

import com.destroflyer.jme3.cubes.BlockNavigator;
import com.destroflyer.jme3.cubes.BlockTerrainControl;
import com.destroflyer.jme3.cubes.Vector3Int;
import com.destrostudios.grid.client.PlayerModel;
import com.destrostudios.grid.client.blocks.BlockAssets;
import com.destrostudios.grid.client.gameproxy.GameProxy;
import com.destrostudios.grid.client.models.ModelObject;
import com.destrostudios.grid.components.*;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.actions.PositionUpdateAction;
import com.destrostudios.grid.actions.SkipRoundAction;
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
import com.simsilica.lemur.Label;
import com.simsilica.lemur.ProgressBar;

import java.util.HashMap;
import java.util.Optional;

public class GameAppState extends BaseAppState implements ActionListener {

    private final GameProxy gameProxy;
    private Node blockTerrainNode;
    private BlockTerrainControl blockTerrainControl;
    private HashMap<Integer, PlayerModel> playerModels = new HashMap<>();
    private HashMap<Integer, ModelObject> treeModels = new HashMap<>();

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

        mainApplication.getInputManager().addMapping("key_delete", new KeyTrigger(KeyInput.KEY_DELETE));
        mainApplication.getInputManager().addMapping("mouse_left", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        mainApplication.getInputManager().addMapping("mouse_right", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        mainApplication.getInputManager().addListener(this, "key_delete", "mouse_left", "mouse_right");

        updateVisuals();
    }

    @Override
    public void update(float tpf) {
        if (gameProxy.update()) {
            updateVisuals();
        }
        super.update(tpf);
    }

    private void updateVisuals() {
        EntityWorld entityWorld = gameProxy.getGame().getWorld();

        blockTerrainControl.removeBlockArea(new Vector3Int(), new Vector3Int(16, 1, 16));
        for (int playerEntity : entityWorld.list(WalkableComponent.class)) {
            PositionComponent positionComponent = entityWorld.getComponent(playerEntity, PositionComponent.class).get();
            blockTerrainControl.setBlock(new Vector3Int(positionComponent.getX(), 0, positionComponent.getY()), BlockAssets.BLOCK_GRASS);
        }

        for (int playerEntity : entityWorld.list(TreeComponent.class)) {
            ModelObject treeModel = treeModels.computeIfAbsent(playerEntity, pe -> {
                ModelObject newTreeModel = new ModelObject(mainApplication.getAssetManager(), "models/tree/skin.xml");
                mainApplication.getRootNode().attachChild(newTreeModel);
                return newTreeModel;
            });

            PositionComponent positionComponent = entityWorld.getComponent(playerEntity, PositionComponent.class).get();
            treeModel.setLocalTranslation((positionComponent.getX() + 0.5f) * 3, 3, (positionComponent.getY() + 0.5f) * 3);
        }

        for (int playerEntity : entityWorld.list(PlayerComponent.class)) {
            PlayerModel playerModel = playerModels.computeIfAbsent(playerEntity, pe -> {
                PlayerModel newPlayerModel = new PlayerModel(mainApplication.getAssetManager());
                mainApplication.getRootNode().attachChild(newPlayerModel.getModelObject());
                mainApplication.getGuiNode().attachChild(newPlayerModel.getLblName());
                mainApplication.getGuiNode().attachChild(newPlayerModel.getHealthBar());
                return newPlayerModel;
            });

            ModelObject modelObject = playerModel.getModelObject();
            PositionComponent positionComponent = entityWorld.getComponent(playerEntity, PositionComponent.class).get();
            HealthPointsComponent healthPointsComponent = entityWorld.getComponent(playerEntity, HealthPointsComponent.class).get();
            MaxHealthComponent maxHealthComponent = entityWorld.getComponent(playerEntity, MaxHealthComponent.class).get();
            modelObject.setLocalTranslation((positionComponent.getX() + 0.5f) * 3, 3, (positionComponent.getY() + 0.5f) * 3);

            Label lblName = playerModel.getLblName();
            String name = entityWorld.getComponent(playerEntity, PlayerComponent.class).get().getName();
            lblName.setText(name);

            ProgressBar healthBar = playerModel.getHealthBar();
            healthBar.setProgressPercent((float) healthPointsComponent.getHealth() / (float) maxHealthComponent.getMaxHealth());
            healthBar.setMessage(String.format("%s / %s", healthPointsComponent.getHealth(), maxHealthComponent.getMaxHealth()));

            playerModel.updateGuiControlPositions(mainApplication.getCamera());
        }
        int activePlayerEntity = entityWorld.list(RoundComponent.class).get(0);
        String activePlayerName = entityWorld.getComponent(activePlayerEntity, PlayerComponent.class).get().getName();
        AttackPointsComponent attackPointsComponent = entityWorld.getComponent(activePlayerEntity, AttackPointsComponent.class).get();
        MovementPointsComponent movementPointsComponent = entityWorld.getComponent(activePlayerEntity, MovementPointsComponent.class).get();

        GuiAppState guiAppState = getAppState(GuiAppState.class);
        guiAppState.setCurrentPlayer(activePlayerName);
        guiAppState.setMP(movementPointsComponent.getMovementPoints());
        guiAppState.setAP(attackPointsComponent.getAttackPoints());
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
            switch (actionName) {
                case "key_delete":
                    trySkipRound();
                    break;
                case "mouse_left":
                case "mouse_right":
                    Vector3Int clickedPosition = getHoveredPosition();
                    if (clickedPosition != null) {
                        PositionComponent positionComponent = gameProxy.getGame().getWorld().getComponent(playerEntity, PositionComponent.class).get();
                        int distance = Math.abs(clickedPosition.getX() - positionComponent.getX()) + Math.abs(clickedPosition.getZ() - positionComponent.getY());
                        if (distance == 1) {
                            gameProxy.requestAction(new PositionUpdateAction(clickedPosition.getX(), clickedPosition.getZ(), Integer.toString(playerEntity)));
                        }
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
        if (buttonIndex == 0) {
            trySkipRound();
        }
    }

    private void trySkipRound() {
        Integer playerEntity = gameProxy.getPlayerEntity();
        if (playerEntity != null) {
            gameProxy.requestAction(new SkipRoundAction(Integer.toString(playerEntity)));
        }
    }
}
