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
import com.destrostudios.grid.client.characters.PlayerVisual;
import com.destrostudios.grid.client.gameproxy.GameProxy;
import com.destrostudios.grid.client.gui.GuiSpell;
import com.destrostudios.grid.client.models.ModelObject;
import com.destrostudios.grid.components.character.PlayerComponent;
import com.destrostudios.grid.components.character.RoundComponent;
import com.destrostudios.grid.components.map.PositionComponent;
import com.destrostudios.grid.components.map.TreeComponent;
import com.destrostudios.grid.components.map.WalkableComponent;
import com.destrostudios.grid.components.properties.*;
import com.destrostudios.grid.components.spells.AttackPointCostComponent;
import com.destrostudios.grid.components.spells.SpellComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.events.*;
import com.destrostudios.grid.eventbus.handler.EventHandler;
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
import java.util.Optional;
import java.util.stream.Collectors;

public class GameAppState extends BaseAppState implements ActionListener {

    private final GameProxy gameProxy;
    private Node blockTerrainNode;
    private BlockTerrainControl blockTerrainControl;
    private HashMap<Integer, PlayerVisual> playerVisuals = new HashMap<>();
    private HashMap<Integer, ModelObject> treeModels = new HashMap<>();
    private List<Animation> playingAnimations = new LinkedList<>();
    private Integer targetingSpellEntity;

    public GameAppState(GameProxy gameProxy) {
        this.gameProxy = gameProxy;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application application) {
        super.initialize(stateManager, application);
        blockTerrainNode = new Node();
        blockTerrainNode.setShadowMode(RenderQueue.ShadowMode.Receive);
        blockTerrainControl = BlockAssets.createNewBlockTerrain(mainApplication, new Vector3Int(1, 1, 1));
        blockTerrainControl.setBlockArea(new Vector3Int(), new Vector3Int(16, 1, 16), BlockAssets.BLOCK_GRASS);
        blockTerrainNode.addControl(blockTerrainControl);
        mainApplication.getRootNode().attachChild(blockTerrainNode);

        Camera camera = mainApplication.getCamera();
        camera.setLocation(new Vector3f(23.15413f, 40.838593f, 66.12133f));
        camera.setRotation(new Quaternion(-8.0925995E-4f, 0.9084759f, -0.4179328f, -0.0017719142f));

        mainApplication.getInputManager().addMapping("key_delete", new KeyTrigger(KeyInput.KEY_DELETE));
        mainApplication.getInputManager().addMapping("mouse_left", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        mainApplication.getInputManager().addMapping("mouse_right", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        mainApplication.getInputManager().addListener(this, "key_delete", "mouse_left", "mouse_right");

        recreateGui();
        updateVisuals();

        gameProxy.addResolvedHandler(Event.class, (event, entityWorldSupplier) -> updateVisuals());

        gameProxy.addPreHandler(MoveEvent.class, (EventHandler<MoveEvent>) (event, entityWorldSupplier) -> {
            int playerEntity = event.getEntity();
            PositionComponent positionComponent = event.getPositionComponent();
            playAnimation(new WalkAnimation(playerVisuals.get(playerEntity), positionComponent.getX(), positionComponent.getY()));
        });
        gameProxy.addPreHandler(HealthPointsChangedEvent.class, (EventHandler<HealthPointsChangedEvent>) (event, entityWorldSupplier) -> {
            int targetEntity = event.getEntity();
            playAnimation(new HealthAnimation(playerVisuals.get(targetEntity), event.getNewHealthPoints()));
        });
        gameProxy.addResolvedHandler(RoundSkippedEvent.class, (event, entityWorldSupplier) -> {
            EntityWorld entityWorld = gameProxy.getGame().getWorld();
            int activePlayerEntity = entityWorld.list(RoundComponent.class).get(0);
            String activePlayerName = entityWorld.getComponent(activePlayerEntity, NameComponent.class).get().getName();
            playAnimation(new AnnouncementAnimation(mainApplication, activePlayerName + "s turn"));
            recreateGui();
        });
    }

    private void recreateGui() {
        Integer playerEntity = gameProxy.getPlayerEntity();
        if (playerEntity == null) {
            // spectating only
            return;
        }

        EntityWorld entityWorld = gameProxy.getGame().getWorld();
        List<SpellComponent> spells = entityWorld.getComponents(playerEntity).stream()
                .filter(c -> c instanceof SpellComponent)
                .map(c -> (SpellComponent) c)
                .collect(Collectors.toList());

        List<GuiSpell> guiSpells = spells.stream()
                .map(spellComponent -> {
                    int spellEntity = spellComponent.getSpell();
                    String name = entityWorld.getComponent(spellEntity, NameComponent.class).get().getName();
                    int apCost = entityWorld.getComponent(spellEntity, AttackPointCostComponent.class).map(AttackPointCostComponent::getAttackPointCosts).orElse(0);
                    return new GuiSpell(name, apCost, () -> {
                        if ((targetingSpellEntity == null) || (targetingSpellEntity != spellEntity)) {
                            targetingSpellEntity = spellEntity;
                        } else {
                            targetingSpellEntity = null;
                        }
                        updateTerrain();
                    });
                })
                .collect(Collectors.toList());

        GameGuiAppState gameGuiAppState = getAppState(GameGuiAppState.class);
        gameGuiAppState.removeAllCurrentPlayerElements();
        gameGuiAppState.createAttributes();
        gameGuiAppState.createSpellButtons(guiSpells);
        gameGuiAppState.createEndTurnButton(this::skipRound);

        updateGui();
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
        for (int playerEntity : entityWorld.list(TreeComponent.class)) {
            ModelObject treeModel = treeModels.computeIfAbsent(playerEntity, pe -> {
                ModelObject newTreeModel = new ModelObject(mainApplication.getAssetManager(), "models/tree/skin.xml");
                mainApplication.getRootNode().attachChild(newTreeModel);
                return newTreeModel;
            });

            PositionComponent positionComponent = entityWorld.getComponent(playerEntity, PositionComponent.class).get();
            treeModel.setLocalTranslation(PositionUtil.get3dCoordinate(positionComponent.getX()), PositionUtil.CHARACTER_Y, PositionUtil.get3dCoordinate(positionComponent.getY()));
        }

        for (int playerEntity : entityWorld.list(PlayerComponent.class)) {
            PlayerVisual playerVisual = playerVisuals.computeIfAbsent(playerEntity, pe -> {
                PlayerVisual newPlayerVisual = new PlayerVisual(mainApplication.getCamera(), mainApplication.getAssetManager());
                mainApplication.getRootNode().attachChild(newPlayerVisual.getModelObject());
                mainApplication.getGuiNode().attachChild(newPlayerVisual.getLblName());
                mainApplication.getGuiNode().attachChild(newPlayerVisual.getHealthBar());
                return newPlayerVisual;
            });

            ModelObject modelObject = playerVisual.getModelObject();
            PositionComponent positionComponent = entityWorld.getComponent(playerEntity, PositionComponent.class).get();
            HealthPointsComponent healthPointsComponent = entityWorld.getComponent(playerEntity, HealthPointsComponent.class).get();
            MaxHealthComponent maxHealthComponent = entityWorld.getComponent(playerEntity, MaxHealthComponent.class).get();
            modelObject.setLocalTranslation(PositionUtil.get3dCoordinate(positionComponent.getX()), 3, PositionUtil.get3dCoordinate(positionComponent.getY()));

            Label lblName = playerVisual.getLblName();
            String name = entityWorld.getComponent(playerEntity, NameComponent.class).get().getName();
            lblName.setText(name);

            playerVisual.setMaximumHealth(maxHealthComponent.getMaxHealth());
            playerVisual.setCurrentHealth(healthPointsComponent.getHealth());
        }

        updateGui();
    }

    private void updateTerrain() {
        EntityWorld entityWorld = gameProxy.getGame().getWorld();
        blockTerrainControl.removeBlockArea(new Vector3Int(), new Vector3Int(16, 1, 16));
        for (int playerEntity : entityWorld.list(WalkableComponent.class)) {
            PositionComponent positionComponent = entityWorld.getComponent(playerEntity, PositionComponent.class).get();
            Block block = BlockAssets.BLOCK_GRASS;
            if ((targetingSpellEntity != null) && isTargetable(targetingSpellEntity, positionComponent.getX(), positionComponent.getY())) {
                block = BlockAssets.BLOCK_GRASS_GOLD_TOP;
            }
            blockTerrainControl.setBlock(new Vector3Int(positionComponent.getX(), 0, positionComponent.getY()), block);
        }
    }

    private boolean isTargetable(int spellEntity, int x, int y) {
        // TODO: Replace with game logic utility
        return (Math.random() < 0.2);
    }

    private void updateGui() {
        EntityWorld entityWorld = gameProxy.getGame().getWorld();
        GameGuiAppState gameGuiAppState = getAppState(GameGuiAppState.class);

        int activePlayerEntity = entityWorld.list(RoundComponent.class).get(0);
        String activePlayerName = entityWorld.getComponent(activePlayerEntity, NameComponent.class).get().getName();
        int activePlayerMP = entityWorld.getComponent(activePlayerEntity, MovementPointsComponent.class).get().getMovementPoints();
        int activePlayerAP = entityWorld.getComponent(activePlayerEntity, AttackPointsComponent.class).get().getAttackPoints();
        gameGuiAppState.setActivePlayerName(activePlayerName);
        gameGuiAppState.setActivePlayerMP(activePlayerMP);
        gameGuiAppState.setActivePlayerAP(activePlayerAP);

        Integer playerEntity = gameProxy.getPlayerEntity();
        // non-spectators
        if (playerEntity != null) {
            int ownPlayerCurrentHealth = entityWorld.getComponent(playerEntity, HealthPointsComponent.class).get().getHealth();
            int ownPlayerMaximumHealth = entityWorld.getComponent(playerEntity, MaxHealthComponent.class).get().getMaxHealth();
            int ownPlayerMP = entityWorld.getComponent(playerEntity, MovementPointsComponent.class).get().getMovementPoints();
            int ownPlayerAP = entityWorld.getComponent(playerEntity, AttackPointsComponent.class).get().getAttackPoints();
            gameGuiAppState.setOwnPlayerHealth(ownPlayerCurrentHealth, ownPlayerMaximumHealth);
            gameGuiAppState.setOwnPlayerMP(ownPlayerMP);
            gameGuiAppState.setOwnPlayerAP(ownPlayerAP);
        }
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
                case "mouse_left":
                case "mouse_right":
                    Vector3Int clickedPosition = getHoveredPosition();
                    if (clickedPosition != null) {
                        if (targetingSpellEntity != null) {
                            EntityWorld world = gameProxy.getGame().getWorld();
                            Optional<Integer> targetEntity = world.list(PlayerComponent.class).stream()
                                    .filter(e -> !world.hasComponents(e, RoundComponent.class))
                                    .findFirst();
                            gameProxy.requestAction(new CastSpellAction(targetEntity.get(), gameProxy.getPlayerEntity().toString(), targetingSpellEntity));
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
