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
import com.destrostudios.grid.components.map.ObstacleComponent;
import com.destrostudios.grid.components.map.PositionComponent;
import com.destrostudios.grid.components.map.VisualComponent;
import com.destrostudios.grid.components.map.WalkableComponent;
import com.destrostudios.grid.components.properties.*;
import com.destrostudios.grid.components.spells.OnCooldownComponent;
import com.destrostudios.grid.components.spells.TooltipComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.events.*;
import com.destrostudios.grid.eventbus.handler.EventHandler;
import com.destrostudios.grid.shared.Characters;
import com.destrostudios.grid.util.CalculationUtils;
import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.collision.CollisionResults;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
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
    private int mapSizeX;
    private int mapSizeY;
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

        calculateMapSize();

        if (map.getEnvironmentBlock() != null) {
            Node environmentNode = new Node();
            environmentNode.setShadowMode(RenderQueue.ShadowMode.Receive);
            BlockTerrainControl environmentBlockTerrainControl = BlockAssets.createNewBlockTerrain(mainApplication, mapSizeX, mapSizeY, new Vector3Int(3, 1, 3));
            // Fill a total of 3x3 of the terrain size
            environmentBlockTerrainControl.setBlockArea(new Vector3Int(0, 1, 0), new Vector3Int(3 * mapSizeX, 1, 3 * mapSizeY), map.getEnvironmentBlock());
            // Remove the "inner" part, where the actual map terran will be
            environmentBlockTerrainControl.removeBlockArea(new Vector3Int(mapSizeX, 1, mapSizeY), new Vector3Int(mapSizeX, 1, mapSizeY));
            // Add a layer below the "inner" part, where the actual map terran will be
            environmentBlockTerrainControl.setBlockArea(new Vector3Int(mapSizeX, 0, mapSizeY), new Vector3Int(mapSizeX, 1, mapSizeY), map.getEnvironmentBlock());
            environmentNode.addControl(environmentBlockTerrainControl);
            environmentNode.setLocalTranslation(-1 * mapSizeX * BlockAssets.BLOCK_SIZE, -1 * BlockAssets.BLOCK_SIZE, -1 * mapSizeY * BlockAssets.BLOCK_SIZE);
            rootNode.attachChild(environmentNode);
        }

        blockTerrainNode = new Node();
        blockTerrainNode.setShadowMode(RenderQueue.ShadowMode.Receive);
        blockTerrainControl = BlockAssets.createNewBlockTerrain(mainApplication, mapSizeX, mapSizeY, new Vector3Int(1, 1, 1));
        blockTerrainNode.addControl(blockTerrainControl);
        rootNode.attachChild(blockTerrainNode);

        Camera camera = mainApplication.getCamera();
        camera.setLocation(map.getCameraPosition());
        camera.setRotation(map.getCameraRotation());

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

    private void calculateMapSize() {
        int maxX = -1;
        int maxY = -1;
        EntityWorld entityWorld = gameProxy.getGame().getWorld();
        for (int groundEntity : entityWorld.list(WalkableComponent.class)) {
            PositionComponent positionComponent = entityWorld.getComponent(groundEntity, PositionComponent.class);
            if (positionComponent.getX() > maxX) {
                maxX = positionComponent.getX();
            }
            if (positionComponent.getY() > maxY) {
                maxY = positionComponent.getY();
            }
        }
        mapSizeX = (maxX + 1);
        mapSizeY = (maxY + 1);
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
        List<Integer> list = entityWorld.list(ObstacleComponent.class).stream()
                .filter(entity -> !entityWorld.hasComponents(entity, PlayerComponent.class))
                .collect(Collectors.toList());
        for (int obstacleEntity : list) {
            ModelObject obstacleModel = obstacleModels.computeIfAbsent(obstacleEntity, pe -> {
                String modelName = entityWorld.getComponent(obstacleEntity, VisualComponent.class).getName();
                ModelObject newObstacleModel = new ModelObject(mainApplication.getAssetManager(), "models/" + modelName + "/skin.xml");
                rootNode.attachChild(newObstacleModel);
                return newObstacleModel;
            });

            PositionComponent positionComponent = entityWorld.getComponent(obstacleEntity, PositionComponent.class);
            obstacleModel.setLocalTranslation(PositionUtil.get3dCoordinate(positionComponent.getX()), PositionUtil.CHARACTER_Y, PositionUtil.get3dCoordinate(positionComponent.getY()));
        }

        for (int playerEntity : entityWorld.list(PlayerComponent.class)) {
            PlayerVisual playerVisual = playerVisuals.computeIfAbsent(playerEntity, pe -> {
                String characterName = Characters.getRandomCharacterName(); // entityWorld.getComponent(playerEntity, VisualComponent.class).getName();
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
        blockTerrainControl.removeBlockArea(new Vector3Int(), new Vector3Int(mapSizeX, 1, mapSizeY));
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
