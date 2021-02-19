package com.destrostudios.grid.client.appstates;

import com.destroflyer.jme3.cubes.Block;
import com.destroflyer.jme3.cubes.BlockNavigator;
import com.destroflyer.jme3.cubes.BlockTerrainControl;
import com.destroflyer.jme3.cubes.Vector3Int;
import com.destrostudios.grid.actions.CastSpellAction;
import com.destrostudios.grid.actions.PositionUpdateAction;
import com.destrostudios.grid.actions.SkipRoundAction;
import com.destrostudios.grid.client.PositionUtil;
import com.destrostudios.grid.client.animations.Animation;
import com.destrostudios.grid.client.animations.AnnouncementAnimation;
import com.destrostudios.grid.client.animations.HealthAnimation;
import com.destrostudios.grid.client.animations.WalkAnimation;
import com.destrostudios.grid.client.blocks.BlockAssets;
import com.destrostudios.grid.client.characters.CharacterModel;
import com.destrostudios.grid.client.characters.CharacterModels;
import com.destrostudios.grid.client.characters.PlayerVisual;
import com.destrostudios.grid.client.gameproxy.GameProxy;
import com.destrostudios.grid.client.gui.GuiSpell;
import com.destrostudios.grid.client.maps.Map;
import com.destrostudios.grid.client.maps.Maps;
import com.destrostudios.grid.client.models.ModelObject;
import com.destrostudios.grid.components.character.PlayerComponent;
import com.destrostudios.grid.components.character.RoundComponent;
import com.destrostudios.grid.components.map.PositionComponent;
import com.destrostudios.grid.components.map.TreeComponent;
import com.destrostudios.grid.components.map.WalkableComponent;
import com.destrostudios.grid.components.properties.*;
import com.destrostudios.grid.components.spells.OnCooldownComponent;
import com.destrostudios.grid.components.spells.TooltipComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.events.*;
import com.destrostudios.grid.eventbus.handler.EventHandler;
import com.destrostudios.grid.util.CalculationUtils;
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
import com.simsilica.lemur.Label;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class GameAppState extends BaseAppState implements ActionListener {

    private GameProxy gameProxy;
    private Map map;
    private Node rootNode;
    private Node guiNode;
    private Node blockTerrainNode;
    private BlockTerrainControl blockTerrainControl;
    private HashMap<Integer, PlayerVisual> playerVisuals = new HashMap<>();
    private HashMap<Integer, ModelObject> obstacleModels = new HashMap<>();
    private LinkedList<Animation> playingAnimations = new LinkedList<>();
    private Integer targetingSpellEntity;

    public GameAppState(GameProxy gameProxy) {
        this.gameProxy = gameProxy;
        map = Maps.get(gameProxy.getStartGameInfo().getMapName());
    }

    @Override
    public void initialize(AppStateManager stateManager, Application application) {
        super.initialize(stateManager, application);
        rootNode = new Node();
        mainApplication.getRootNode().attachChild(rootNode);

        guiNode = new Node();
        mainApplication.getGuiNode().attachChild(guiNode);

        if (map.getEnvironmentBlock() != null) {
            Node environmentNode = new Node();
            environmentNode.setShadowMode(RenderQueue.ShadowMode.Receive);
            BlockTerrainControl environmentBlockTerrainControl = BlockAssets.createNewBlockTerrain(mainApplication, new Vector3Int(3, 1, 3));
            environmentBlockTerrainControl.setBlockArea(new Vector3Int(0, 1, 0), new Vector3Int(48, 1, 48), map.getEnvironmentBlock());
            environmentBlockTerrainControl.removeBlockArea(new Vector3Int(16, 1, 16), new Vector3Int(16, 1, 16));
            environmentBlockTerrainControl.setBlockArea(new Vector3Int(16, 0, 16), new Vector3Int(16, 1, 16), map.getEnvironmentBlock());
            environmentNode.addControl(environmentBlockTerrainControl);
            environmentNode.setLocalTranslation(-48, -3, -48);
            rootNode.attachChild(environmentNode);
        }

        blockTerrainNode = new Node();
        blockTerrainNode.setShadowMode(RenderQueue.ShadowMode.Receive);
        blockTerrainControl = BlockAssets.createNewBlockTerrain(mainApplication, new Vector3Int(1, 1, 1));
        blockTerrainNode.addControl(blockTerrainControl);
        rootNode.attachChild(blockTerrainNode);

        Camera camera = mainApplication.getCamera();
        camera.setLocation(new Vector3f(23.15413f, 40.838593f, 66.12133f));
        camera.setRotation(new Quaternion(-8.0925995E-4f, 0.9084759f, -0.4179328f, -0.0017719142f));

        mainApplication.getInputManager().addMapping("key_delete", new KeyTrigger(KeyInput.KEY_DELETE));
        mainApplication.getInputManager().addMapping("mouse_left", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        mainApplication.getInputManager().addMapping("mouse_right", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        mainApplication.getInputManager().addListener(this, "key_delete", "mouse_left", "mouse_right");

        updateVisuals();

        gameProxy.addResolvedHandler(Event.class, (event, entityWorldSupplier) -> updateVisuals());

        gameProxy.addPreHandler(MoveEvent.class, (EventHandler<MoveEvent>) (event, entityWorldSupplier) -> {
            int playerEntity = event.getEntity();
            PositionComponent positionComponent = event.getPositionComponent();
            playAnimation(new WalkAnimation(playerVisuals.get(playerEntity), positionComponent.getX(), positionComponent.getY()));
        });
        gameProxy.addPreHandler(PropertiePointsChangedEvent.HealthPointsChangedEvent.class, (EventHandler<PropertiePointsChangedEvent.HealthPointsChangedEvent>) (event, entityWorldSupplier) -> {
            int targetEntity = event.getEntity();
            playAnimation(new HealthAnimation(playerVisuals.get(targetEntity), event.getNewPoints()));
        });
        gameProxy.addResolvedHandler(SimpleUpdateEvent.RoundSkippedEvent.class, (event, entityWorldSupplier) -> {
            EntityWorld entityWorld = gameProxy.getGame().getWorld();
            int activePlayerEntity = entityWorld.list(RoundComponent.class).get(0);
            String activePlayerName = entityWorld.getComponent(activePlayerEntity, NameComponent.class).getName();
            playAnimation(new AnnouncementAnimation(mainApplication, activePlayerName + "s turn"));
        });
        gameProxy.addResolvedHandler(GameOverEvent.class, (EventHandler<GameOverEvent>) (event, entityWorldSupplier) -> {
            getAppState(GameGuiAppState.class).onGameOver("Team #" + event.getWinnerTeam());
        });
    }

    @Override
    public void update(float tpf) {
        do {
            while (gameProxy.triggeredHandlersInQueue() && playingAnimations.isEmpty()) {
                gameProxy.triggerNextHandler();
            }
        } while (gameProxy.applyNextAction());
        for (Animation animation : playingAnimations.toArray(new Animation[0])) {
            animation.update(tpf);
            if (animation.isFinished()) {
                animation.end();
                playingAnimations.remove(animation);
            }
        }
        super.update(tpf);
    }

    private void updateVisuals() {
        targetingSpellEntity = null;
        updateTerrain();

        EntityWorld entityWorld = gameProxy.getGame().getWorld();
        for (int obstacleEntity : entityWorld.list(TreeComponent.class)) {
            ModelObject obstacleModel = obstacleModels.computeIfAbsent(obstacleEntity, pe -> {
                String modelName = (gameProxy.getStartGameInfo().getMapName().equals("desert") ? "rock" : "tree"); // entityWorld.getComponent(obstacleEntity, VisualComponent.class).getName();
                ModelObject newObstacleModel = new ModelObject(mainApplication.getAssetManager(), "models/" + modelName + "/skin.xml");
                rootNode.attachChild(newObstacleModel);
                return newObstacleModel;
            });

            PositionComponent positionComponent = entityWorld.getComponent(obstacleEntity, PositionComponent.class);
            obstacleModel.setLocalTranslation(PositionUtil.get3dCoordinate(positionComponent.getX()), PositionUtil.CHARACTER_Y, PositionUtil.get3dCoordinate(positionComponent.getY()));
        }

        for (int playerEntity : entityWorld.list(PlayerComponent.class)) {
            PlayerVisual playerVisual = playerVisuals.computeIfAbsent(playerEntity, pe -> {
                String[] characterNames = new String[] { "aland", "alice", "dosaz", "dwarf_warrior", "elven_archer", "garmon", "scarlet", "tristan" };
                String characterName = characterNames[(int) (Math.random() * characterNames.length)]; // entityWorld.getComponent(playerEntity, VisualComponent.class).getName();
                CharacterModel characterModel = CharacterModels.get(characterName);
                PlayerVisual newPlayerVisual = new PlayerVisual(mainApplication.getCamera(), mainApplication.getAssetManager(), characterModel);
                rootNode.attachChild(newPlayerVisual.getModelObject());
                guiNode.attachChild(newPlayerVisual.getLblName());
                guiNode.attachChild(newPlayerVisual.getHealthBar());
                return newPlayerVisual;
            });

            ModelObject modelObject = playerVisual.getModelObject();
            PositionComponent positionComponent = entityWorld.getComponent(playerEntity, PositionComponent.class);
            HealthPointsComponent healthPointsComponent = entityWorld.getComponent(playerEntity, HealthPointsComponent.class);
            MaxHealthComponent maxHealthComponent = entityWorld.getComponent(playerEntity, MaxHealthComponent.class);
            modelObject.setLocalTranslation(PositionUtil.get3dCoordinate(positionComponent.getX()), 3, PositionUtil.get3dCoordinate(positionComponent.getY()));

            Label lblName = playerVisual.getLblName();
            String name = entityWorld.getComponent(playerEntity, NameComponent.class).getName();
            lblName.setText(name);

            playerVisual.setMaximumHealth(maxHealthComponent.getMaxHealth());
            playerVisual.setCurrentHealth(healthPointsComponent.getHealth());
        }

        updateGui();
    }

    private void updateTerrain() {
        EntityWorld entityWorld = gameProxy.getGame().getWorld();
        blockTerrainControl.removeBlockArea(new Vector3Int(), new Vector3Int(16, 1, 16));
        List<Integer> rangeGroundEntities = targetingSpellEntity != null
                ? CalculationUtils.getRange(targetingSpellEntity, gameProxy.getPlayerEntity(), entityWorld)
                : new LinkedList<>();
        for (int groundEntity : entityWorld.list(WalkableComponent.class)) {
            PositionComponent positionComponent = entityWorld.getComponent(groundEntity, PositionComponent.class);
            Block block = map.getTerrainBlock().getBlockTopGrid();
            if (rangeGroundEntities.contains(groundEntity)) {
                block = map.getTerrainBlock().getBlockTopTarget();
            }
            blockTerrainControl.setBlock(new Vector3Int(positionComponent.getX(), 0, positionComponent.getY()), block);
        }
    }

    private void updateGui() {
        Integer playerEntity = gameProxy.getPlayerEntity();
        if (playerEntity == null) {
            // spectating only
            return;
        }

        GameGuiAppState gameGuiAppState = getAppState(GameGuiAppState.class);
        EntityWorld entityWorld = gameProxy.getGame().getWorld();

        int activePlayerEntity = entityWorld.list(RoundComponent.class).get(0);
        String activePlayerName = entityWorld.getComponent(activePlayerEntity, NameComponent.class).getName();
        int activePlayerMP = entityWorld.getComponent(activePlayerEntity, MovementPointsComponent.class).getMovementPoints();
        int activePlayerAP = entityWorld.getComponent(activePlayerEntity, AttackPointsComponent.class).getAttackPoints();
        gameGuiAppState.setActivePlayerName(activePlayerName);
        gameGuiAppState.setActivePlayerMP(activePlayerMP);
        gameGuiAppState.setActivePlayerAP(activePlayerAP);

        gameGuiAppState.removeAllCurrentPlayerElements();

        gameGuiAppState.createAttributes();
        int ownPlayerCurrentHealth = entityWorld.getComponent(playerEntity, HealthPointsComponent.class).getHealth();
        int ownPlayerMaximumHealth = entityWorld.getComponent(playerEntity, MaxHealthComponent.class).getMaxHealth();
        int ownPlayerMP = entityWorld.getComponent(playerEntity, MovementPointsComponent.class).getMovementPoints();
        int ownPlayerAP = entityWorld.getComponent(playerEntity, AttackPointsComponent.class).getAttackPoints();
        gameGuiAppState.setOwnPlayerHealth(ownPlayerCurrentHealth, ownPlayerMaximumHealth);
        gameGuiAppState.setOwnPlayerMP(ownPlayerMP);
        gameGuiAppState.setOwnPlayerAP(ownPlayerAP);

        SpellsComponent spells = entityWorld.getComponent(playerEntity, SpellsComponent.class);
        List<GuiSpell> guiSpells = spells.getSpells().stream()
                .map(spellEntity -> {
                    String name = entityWorld.getComponent(spellEntity, NameComponent.class).getName();
                    String tooltip = entityWorld.getComponent(spellEntity, TooltipComponent.class).getTooltip();
                    Integer remainingCooldown = entityWorld.hasComponents(spellEntity, OnCooldownComponent.class)
                            ? entityWorld.getComponent(spellEntity, OnCooldownComponent.class).getRemainingRounds()
                            : null;
                    return new GuiSpell(name, tooltip, remainingCooldown, () -> {
                        if ((targetingSpellEntity == null) || (!targetingSpellEntity.equals(spellEntity))) {
                            targetingSpellEntity = spellEntity;
                        } else {
                            targetingSpellEntity = null;
                        }
                        updateTerrain();
                    });
                })
                .collect(Collectors.toList());
        gameGuiAppState.createSpellButtons(guiSpells);

        gameGuiAppState.createEndTurnButton(this::skipRound);
    }

    @Override
    public void cleanup() {
        super.cleanup();
        mainApplication.getRootNode().detachChild(rootNode);
        mainApplication.getGuiNode().detachChild(guiNode);
        mainApplication.getInputManager().removeListener(this);
        gameProxy.cleanupGame();
    }

    @Override
    public void onAction(String actionName, boolean isPressed, float tpf) {
        Integer playerEntity = gameProxy.getPlayerEntity();
        if (playerEntity == null) {
            // spectating only
            return;
        }
        if (isPressed) {
            switch (actionName) {
                case "mouse_left":
                case "mouse_right":
                    Vector3Int clickedPosition = getHoveredPosition();
                    if (clickedPosition != null) {
                        if (targetingSpellEntity != null) {
                            gameProxy.requestAction(new CastSpellAction(clickedPosition.getX(), clickedPosition.getZ(),
                                    gameProxy.getPlayerEntity().toString(), targetingSpellEntity));
                        } else {
                            gameProxy.requestAction(new PositionUpdateAction(clickedPosition.getX(), clickedPosition.getZ(), playerEntity.toString()));
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

    public void playAnimation(Animation animation) {
        animation.start();
        playingAnimations.add(animation);
    }

    private void skipRound() {
        gameProxy.requestAction(new SkipRoundAction(gameProxy.getPlayerEntity().toString()));
    }
}
