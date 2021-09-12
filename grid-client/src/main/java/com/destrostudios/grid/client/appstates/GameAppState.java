package com.destrostudios.grid.client.appstates;

import com.destroflyer.jme3.cubes.Vector3Int;
import com.destrostudios.grid.actions.CastSpellAction;
import com.destrostudios.grid.actions.PositionUpdateAction;
import com.destrostudios.grid.actions.SkipRoundAction;
import com.destrostudios.grid.client.ClientApplication;
import com.destrostudios.grid.client.JMonkeyUtil;
import com.destrostudios.grid.client.animations.*;
import com.destrostudios.grid.client.characters.CustomBlockingAnimationInfo;
import com.destrostudios.grid.client.characters.CastAnimations;
import com.destrostudios.grid.client.characters.EntityVisual;
import com.destrostudios.grid.client.gameproxy.GameProxy;
import com.destrostudios.grid.client.gui.GuiNextPlayer;
import com.destrostudios.grid.client.gui.GuiSpell;
import com.destrostudios.grid.components.character.ActiveTurnComponent;
import com.destrostudios.grid.components.character.NextTurnComponent;
import com.destrostudios.grid.components.map.PositionComponent;
import com.destrostudios.grid.components.map.WalkableComponent;
import com.destrostudios.grid.components.properties.*;
import com.destrostudios.grid.components.spells.base.TooltipComponent;
import com.destrostudios.grid.components.spells.limitations.OnCooldownComponent;
import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.eventbus.Event;
import com.destrostudios.grid.eventbus.EventHandler;
import com.destrostudios.grid.eventbus.action.die.DieEvent;
import com.destrostudios.grid.eventbus.action.gameover.GameOverEvent;
import com.destrostudios.grid.eventbus.action.move.MoveEvent;
import com.destrostudios.grid.eventbus.action.move.MoveType;
import com.destrostudios.grid.eventbus.action.spellcasted.SpellCastedEvent;
import com.destrostudios.grid.eventbus.update.hp.HealthPointsChangedEvent;
import com.destrostudios.grid.eventbus.update.turn.UpdatedTurnEvent;
import com.destrostudios.grid.shared.PlayerInfo;
import com.destrostudios.grid.util.SpellUtils;
import com.destrostudios.turnbasedgametools.grid.Pathfinder;
import com.destrostudios.turnbasedgametools.grid.Position;
import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.destrostudios.grid.util.RangeUtils.getAllEntitiesInRange;
import static com.destrostudios.grid.util.RangeUtils.getAllTargetableEntitiesInRange;

public class GameAppState extends BaseAppState<ClientApplication> implements ActionListener {

    private GameProxy gameProxy;
    private LinkedList<Animation> playingAnimations = new LinkedList<>();
    private Integer targetingSpellEntity;
    private List<Integer> validSpellTargetEntities;
    private Vector3Int hoveredPosition;

    public GameAppState(GameProxy gameProxy) {
        this.gameProxy = gameProxy;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application application) {
        super.initialize(stateManager, application);

        updateVisuals();

        gameProxy.addPreHandler(MoveEvent.class, (EventHandler<MoveEvent>) (event, entityDataSupplier) -> {
            EntityVisual entityVisual = getAppState(MapAppState.class).getEntityVisual(event.getEntity());
            PositionComponent positionComponent = event.getPositionComponent();
            if (event.getMoveType() == MoveType.WALK) {
                playAnimation(new WalkAnimation(entityVisual, positionComponent.getX(), positionComponent.getY(), 8));
            } else if (event.getMoveType() != MoveType.TELEPORT) {
                playAnimation(new MoveAnimation(entityVisual, positionComponent.getX(), positionComponent.getY(), 30));
            }
        });
        gameProxy.addPreHandler(HealthPointsChangedEvent.class, (EventHandler<HealthPointsChangedEvent>) (event, entityDataSupplier) -> {
            EntityData entityData = entityDataSupplier.get();
            int currentHealth = entityData.getComponent(event.getEntity(), HealthPointsComponent.class).getHealth();
            int newHealth = event.getNewPoints();
            if (currentHealth != newHealth) {
                EntityVisual entityVisual = getAppState(MapAppState.class).getEntityVisual(event.getEntity());
                playAnimation(new HealthAnimation(entityVisual, newHealth));
            } else {
                System.err.println("HealthPointsChangedEvent, where health didn't change...");
            }
        });
        gameProxy.addPreHandler(SpellCastedEvent.class, (EventHandler<SpellCastedEvent>) (event, entityDataSupplier) -> {
            EntityData entityData = entityDataSupplier.get();
            EntityVisual entityVisual = getAppState(MapAppState.class).getEntityVisual(event.getPlayerEntity());
            String spellName = entityData.getComponent(event.getSpell(), NameComponent.class).getName();
            CustomBlockingAnimationInfo castAnimation = CastAnimations.get(spellName);
            if (castAnimation != null) {
                lookAt(entityData, event.getPlayerEntity(), entityVisual.getModelObject(), event.getX(), event.getY());
                playAnimation(new CustomBlockingModelAnimation(entityVisual, castAnimation));
            }
        });
        gameProxy.addResolvedHandler(Event.class, (event, entityDataSupplier) -> updateVisuals());
        gameProxy.addResolvedHandler(UpdatedTurnEvent.class, (event, entityDataSupplier) -> {
            EntityData entityData = entityDataSupplier.get();
            int activePlayerEntity = entityData.list(ActiveTurnComponent.class).get(0);
            String activePlayerName = entityData.getComponent(activePlayerEntity, NameComponent.class).getName();
            playAnimation(new AnnouncementAnimation(mainApplication, activePlayerName + "s turn"));
        });
        gameProxy.addResolvedHandler(DieEvent.class, (EventHandler<DieEvent>) (event, entityDataSupplier) -> {
            EntityVisual entityVisual = getAppState(MapAppState.class).getEntityVisual(event.getEntity());
            entityVisual.playDeathAnimation();
            entityVisual.setColor(new ColorRGBA(1, 1, 1, 0.25f));
        });
        gameProxy.addResolvedHandler(GameOverEvent.class, (EventHandler<GameOverEvent>) (event, entityDataSupplier) -> {
            getAppState(GameGuiAppState.class).onGameOver("Team #" + event.getWinnerTeam());
        });

        mainApplication.getInputManager().addMapping("key_1", new KeyTrigger(KeyInput.KEY_1));
        mainApplication.getInputManager().addMapping("key_2", new KeyTrigger(KeyInput.KEY_2));
        mainApplication.getInputManager().addMapping("key_3", new KeyTrigger(KeyInput.KEY_3));
        mainApplication.getInputManager().addMapping("key_4", new KeyTrigger(KeyInput.KEY_4));
        mainApplication.getInputManager().addMapping("key_5", new KeyTrigger(KeyInput.KEY_5));
        mainApplication.getInputManager().addMapping("key_6", new KeyTrigger(KeyInput.KEY_6));
        mainApplication.getInputManager().addMapping("key_7", new KeyTrigger(KeyInput.KEY_7));
        mainApplication.getInputManager().addMapping("key_8", new KeyTrigger(KeyInput.KEY_8));
        mainApplication.getInputManager().addMapping("key_9", new KeyTrigger(KeyInput.KEY_9));
        mainApplication.getInputManager().addMapping("key_space", new KeyTrigger(KeyInput.KEY_SPACE));
        mainApplication.getInputManager().addMapping("mouse_left", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        mainApplication.getInputManager().addMapping("mouse_right", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        mainApplication.getInputManager().addListener(this, "key_1", "key_2", "key_3", "key_4", "key_5", "key_6", "key_7", "key_8", "key_9", "key_space", "mouse_left", "mouse_right");
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        updateAnimations(tpf);
        updateGame();
        updateHoveredPosition();
    }

    private void updateAnimations(float tpf) {
        for (Animation animation : playingAnimations.toArray(Animation[]::new)) {
            animation.update(tpf);
            if (animation.isFinished()) {
                animation.end();
                playingAnimations.remove(animation);
            }
        }
    }

    private void updateGame() {
        do {
            while (gameProxy.triggeredHandlersInQueue() && playingAnimations.stream().noneMatch(Animation::isBlocking)) {
                gameProxy.triggerNextHandler();
            }
        } while (gameProxy.applyNextAction());
    }

    private void updateHoveredPosition() {
        Vector3Int newHoveredPosition = getAppState(MapAppState.class).getHoveredPosition(false);
        boolean hasHoveredPositionChanged = (!Objects.equals(hoveredPosition, newHoveredPosition));
        hoveredPosition = newHoveredPosition;
        if (hasHoveredPositionChanged) {
            updateImpactedAndReachableGroundEntities();
        }
    }

    private void updateVisuals() {
        setTargetingSpell(null);
        getAppState(MapAppState.class).updateVisuals();
    }

    private void selectOrDeselectTargetingSpell(int spellEntity) {
        if ((targetingSpellEntity == null) || (!targetingSpellEntity.equals(spellEntity))) {
            setTargetingSpell(spellEntity);
        } else {
            setTargetingSpell(null);
        }
    }

    private void setTargetingSpell(Integer spellEntity) {
        targetingSpellEntity = spellEntity;
        updateValidAndInvalidGroundEntities();
        updateImpactedAndReachableGroundEntities();
        updateGui();
    }

    private void updateValidAndInvalidGroundEntities() {
        List<Integer> invalidSpellTargetEntities;
        if (targetingSpellEntity != null) {
            validSpellTargetEntities = getAllTargetableEntitiesInRange(targetingSpellEntity, gameProxy.getPlayerEntity(), gameProxy.getGame().getData());
            invalidSpellTargetEntities = getAllEntitiesInRange(targetingSpellEntity, gameProxy.getPlayerEntity(), gameProxy.getGame().getData());
            invalidSpellTargetEntities.removeAll(validSpellTargetEntities);
        } else {
            validSpellTargetEntities = new LinkedList<>();
            invalidSpellTargetEntities = new LinkedList<>();
        }
        getAppState(MapAppState.class).setValidGroundEntities(validSpellTargetEntities, invalidSpellTargetEntities);
    }

    private void updateImpactedAndReachableGroundEntities() {
        LinkedList<Integer> impactedGroundEntities = new LinkedList<>();
        LinkedList<Integer> reachableGroundEntities = new LinkedList<>();
        if ((gameProxy.getPlayerEntity() != null) && (hoveredPosition != null)) {
            EntityData data = gameProxy.getGame().getData();
            // Impacted
            if (targetingSpellEntity != null) {
                List<Integer> affectedWalkableEntities = SpellUtils.getAffectedWalkableEntities(
                    targetingSpellEntity,
                    data.getComponent(gameProxy.getPlayerEntity(), PositionComponent.class),
                    new PositionComponent(hoveredPosition.getX(), hoveredPosition.getZ()),
                    data
                );
                impactedGroundEntities.addAll(affectedWalkableEntities);
            } else {
                // Reachable
                if (data.hasComponents(gameProxy.getPlayerEntity(), ActiveTurnComponent.class) && (!gameProxy.triggeredHandlersInQueue())) {
                    Optional<List<Position>> path = findPathToHoveredPosition();
                    if (path.isPresent()) {
                        reachableGroundEntities.addAll(data.list(WalkableComponent.class).stream()
                            .filter(groundEntity -> {
                                PositionComponent positionComponent = data.getComponent(groundEntity, PositionComponent.class);
                                return path.get().contains(new Position(positionComponent.getX(), positionComponent.getY()));
                            })
                            .collect(Collectors.toSet()));
                    }
                }
            }
        }
        getAppState(MapAppState.class).setImpactedGroundEntities(impactedGroundEntities);
        getAppState(MapAppState.class).setReachableGroundEntities(reachableGroundEntities);
    }

    private void updateGui() {
        Integer playerEntity = gameProxy.getPlayerEntity();
        if (playerEntity == null) {
            // Spectating only
            return;
        }

        GameGuiAppState gameGuiAppState = getAppState(GameGuiAppState.class);
        EntityData entityData = gameProxy.getGame().getData();

        int activePlayerEntity = entityData.list(ActiveTurnComponent.class).get(0);
        String activePlayerName = entityData.getComponent(activePlayerEntity, NameComponent.class).getName();
        int activePlayerMP = entityData.getComponent(activePlayerEntity, MovementPointsComponent.class).getMovementPoints();
        int activePlayerAP = entityData.getComponent(activePlayerEntity, AttackPointsComponent.class).getAttackPoints();
        gameGuiAppState.setActivePlayerName(activePlayerName);
        gameGuiAppState.setActivePlayerMP(activePlayerMP);
        gameGuiAppState.setActivePlayerAP(activePlayerAP);

        GuiNextPlayer[] guiNextPlayers = new GuiNextPlayer[GameGuiAppState.DISPLAYED_NEXT_PLAYERS];
        int nextPlayerEntity = activePlayerEntity;
        for (int i = 0; i < guiNextPlayers.length; i++) {
            String playerName = entityData.getComponent(nextPlayerEntity, NameComponent.class).getName();
            PlayerInfo playerInfo = gameProxy.getStartGameInfo().getTeam1().stream()
                .filter(currentPlayerInfo -> currentPlayerInfo.getLogin().equals(playerName))
                .findFirst()
                .orElse(gameProxy.getStartGameInfo().getTeam2().stream()
                        .filter(currentPlayerInfo -> currentPlayerInfo.getLogin().equals(playerName))
                        .findFirst()
                        .orElse(null));
            guiNextPlayers[i] = new GuiNextPlayer(playerName, playerInfo.getCharacterName());
            nextPlayerEntity = entityData.getComponent(nextPlayerEntity, NextTurnComponent.class).getNextPlayer();
        }
        gameGuiAppState.setNextPlayers(guiNextPlayers);

        gameGuiAppState.removeAllCurrentPlayerElements();

        gameGuiAppState.createAttributes();
        int ownPlayerCurrentHealth = entityData.getComponent(playerEntity, HealthPointsComponent.class).getHealth();
        int ownPlayerMaximumHealth = entityData.getComponent(playerEntity, MaxHealthComponent.class).getMaxHealth();
        int ownPlayerMP = entityData.getComponent(playerEntity, MovementPointsComponent.class).getMovementPoints();
        int ownPlayerAP = entityData.getComponent(playerEntity, AttackPointsComponent.class).getAttackPoints();
        gameGuiAppState.setOwnPlayerHealth(ownPlayerCurrentHealth, ownPlayerMaximumHealth);
        gameGuiAppState.setOwnPlayerMP(ownPlayerMP);
        gameGuiAppState.setOwnPlayerAP(ownPlayerAP);

        SpellsComponent spells = entityData.getComponent(playerEntity, SpellsComponent.class);
        List<GuiSpell> guiSpells = spells.getSpells().stream()
                .map(spellEntity -> {
                    String name = entityData.getComponent(spellEntity, NameComponent.class).getName();
                    String tooltip = entityData.getComponent(spellEntity, TooltipComponent.class).getTooltip();
                    Integer remainingCooldown = entityData.hasComponents(spellEntity, OnCooldownComponent.class)
                            ? entityData.getComponent(spellEntity, OnCooldownComponent.class).getRemainingRounds()
                            : null;
                    boolean isCastable = SpellUtils.isCastable(playerEntity, spellEntity, entityData);
                    boolean isTargeting = Objects.equals(targetingSpellEntity, spellEntity);
                    return new GuiSpell(name, tooltip, remainingCooldown, isCastable, isTargeting, () -> {
                        if (!gameProxy.triggeredHandlersInQueue()) {
                            selectOrDeselectTargetingSpell(spellEntity);
                        }
                    });
                })
                .collect(Collectors.toList());
        gameGuiAppState.createSpellButtons(guiSpells);

        gameGuiAppState.createEndTurnButton(this::endTurn);
    }

    @Override
    public void cleanup() {
        super.cleanup();
        mainApplication.getInputManager().removeListener(this);
        gameProxy.cleanupGame();
    }

    @Override
    public void onAction(String actionName, boolean isPressed, float tpf) {
        Integer playerEntity = gameProxy.getPlayerEntity();
        if (playerEntity == null) {
            // Spectating only
            return;
        }
        if (isPressed && (!gameProxy.triggeredHandlersInQueue())) {
            EntityData entityData = gameProxy.getGame().getData();
            switch (actionName) {
                case "mouse_left":
                case "mouse_right":
                    updateHoveredPosition();
                    if (hoveredPosition != null) {
                        if (targetingSpellEntity != null) {
                            if (containsEntity(entityData, validSpellTargetEntities, hoveredPosition.getX(), hoveredPosition.getZ())) {
                                gameProxy.requestAction(new CastSpellAction(
                                    hoveredPosition.getX(),
                                    hoveredPosition.getZ(),
                                    gameProxy.getPlayerEntity().toString(), targetingSpellEntity)
                                );
                            }
                            setTargetingSpell(null);
                        } else {
                            Optional<List<Position>> path = findPathToHoveredPosition();
                            if (path.isPresent()) {
                                for (Position step : path.get()) {
                                    gameProxy.requestAction(new PositionUpdateAction(step.x, step.y, playerEntity.toString()));
                                }
                            }
                        }
                    }
                    break;
                case "key_space":
                    endTurn();
                    break;
                default:
                    if (actionName.startsWith("key_")) {
                        SpellsComponent spellsComponent = entityData.getComponent(playerEntity, SpellsComponent.class);
                        if (spellsComponent != null) {
                            int spellIndex = (Integer.parseInt(actionName.substring(4)) - 1);
                            if (spellIndex < spellsComponent.getSpells().size()) {
                                int spellEntity = spellsComponent.getSpells().get(spellIndex);
                                if (SpellUtils.isCastable(playerEntity, spellEntity, entityData)) {
                                    selectOrDeselectTargetingSpell(spellEntity);
                                }
                            }
                        }
                    }
            }
        }
    }

    private void lookAt(EntityData entityData, int sourceEntity, Spatial sourceSpatial, int targetX, int targetY) {
        PositionComponent sourcePositionComponent = entityData.getComponent(sourceEntity, PositionComponent.class);
        float distanceX = (targetX - sourcePositionComponent.getX());
        float distanceY = (targetY - sourcePositionComponent.getY());
        if ((distanceX != 0) || (distanceY != 0)) {
            JMonkeyUtil.lookAtDirection(sourceSpatial, new Vector3f(distanceX, 0, distanceY));
        }
    }

    private boolean containsEntity(EntityData entityData, List<Integer> entities, int x, int y) {
        return entities.stream().anyMatch(entity -> {
            PositionComponent positionComponent = entityData.getComponent(entity, PositionComponent.class);
            return ((positionComponent.getX() == x) && (positionComponent.getY() == y));
        });
    }

    private Optional<List<Position>> findPathToHoveredPosition() {
        EntityData data = gameProxy.getGame().getData();
        PositionComponent playerPosition = data.getComponent(gameProxy.getPlayerEntity(), PositionComponent.class);
        return new Pathfinder().findPath(
                p -> SpellUtils.isPositionIsFree(data, new PositionComponent(p.x, p.y), gameProxy.getPlayerEntity()),
                new Position(playerPosition.getX(), playerPosition.getY()),
                new Position(hoveredPosition.getX(), hoveredPosition.getZ()),
                data.getComponent(gameProxy.getPlayerEntity(), MovementPointsComponent.class).getMovementPoints());
    }

    public void playAnimation(Animation animation) {
        animation.start();
        playingAnimations.add(animation);
    }

    private void endTurn() {
        gameProxy.requestAction(new SkipRoundAction(gameProxy.getPlayerEntity().toString()));
    }
}
