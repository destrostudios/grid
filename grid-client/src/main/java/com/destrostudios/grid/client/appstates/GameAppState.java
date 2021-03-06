package com.destrostudios.grid.client.appstates;

import com.destroflyer.jme3.cubes.Vector3Int;
import com.destrostudios.grid.actions.CastSpellAction;
import com.destrostudios.grid.actions.PositionUpdateAction;
import com.destrostudios.grid.actions.SkipRoundAction;
import com.destrostudios.grid.client.ClientApplication;
import com.destrostudios.grid.client.animations.Animation;
import com.destrostudios.grid.client.animations.AnnouncementAnimation;
import com.destrostudios.grid.client.animations.HealthAnimation;
import com.destrostudios.grid.client.animations.WalkAnimation;
import com.destrostudios.grid.client.characters.PlayerVisual;
import com.destrostudios.grid.client.gameproxy.GameProxy;
import com.destrostudios.grid.client.gui.GuiSpell;
import com.destrostudios.grid.components.character.TurnComponent;
import com.destrostudios.grid.components.map.PositionComponent;
import com.destrostudios.grid.components.properties.*;
import com.destrostudios.grid.components.spells.limitations.OnCooldownComponent;
import com.destrostudios.grid.components.spells.base.TooltipComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.Event;
import com.destrostudios.grid.eventbus.EventHandler;
import com.destrostudios.grid.eventbus.action.gameover.GameOverEvent;
import com.destrostudios.grid.eventbus.action.walk.WalkEvent;
import com.destrostudios.grid.eventbus.update.hp.HealthPointsChangedEvent;
import com.destrostudios.grid.eventbus.update.turn.UpdatedTurnEvent;
import com.destrostudios.grid.util.RangeUtils;
import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class GameAppState extends BaseAppState<ClientApplication> implements ActionListener {

    private GameProxy gameProxy;
    private LinkedList<Animation> playingAnimations = new LinkedList<>();
    private Integer targetingSpellEntity;
    private List<Integer> validSpellTargetEntities = new LinkedList<>();

    public GameAppState(GameProxy gameProxy) {
        this.gameProxy = gameProxy;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application application) {
        super.initialize(stateManager, application);

        updateVisuals();

        gameProxy.addPreHandler(WalkEvent.class, (EventHandler<WalkEvent>) (event, entityWorldSupplier) -> {
            PlayerVisual playerVisual = getAppState(MapAppState.class).getPlayerVisual(event.getEntity());
            PositionComponent positionComponent = event.getPositionComponent();
            playAnimation(new WalkAnimation(playerVisual, positionComponent.getX(), positionComponent.getY()));
        });
        gameProxy.addPreHandler(HealthPointsChangedEvent.class, (EventHandler<HealthPointsChangedEvent>) (event, entityWorldSupplier) -> {
            PlayerVisual playerVisual = getAppState(MapAppState.class).getPlayerVisual(event.getEntity());
            playAnimation(new HealthAnimation(playerVisual, event.getNewPoints()));
        });
        gameProxy.addResolvedHandler(Event.class, (event, entityWorldSupplier) -> updateVisuals());
        gameProxy.addResolvedHandler(UpdatedTurnEvent.class, (event, entityWorldSupplier) -> {
            EntityWorld entityWorld = gameProxy.getGame().getWorld();
            int activePlayerEntity = entityWorld.list(TurnComponent.class).get(0);
            String activePlayerName = entityWorld.getComponent(activePlayerEntity, NameComponent.class).getName();
            playAnimation(new AnnouncementAnimation(mainApplication, activePlayerName + "s turn"));
        });
        gameProxy.addResolvedHandler(GameOverEvent.class, (EventHandler<GameOverEvent>) (event, entityWorldSupplier) -> {
            getAppState(GameGuiAppState.class).onGameOver("Team #" + event.getWinnerTeam());
        });

        mainApplication.getInputManager().addMapping("mouse_left", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        mainApplication.getInputManager().addMapping("mouse_right", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        mainApplication.getInputManager().addListener(this, "mouse_left", "mouse_right");
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
        MapAppState mapAppState = getAppState(MapAppState.class);
        mapAppState.clearValidTargetEntities();
        mapAppState.updateVisuals();
        updateGui();
    }

    private void updateGui() {
        Integer playerEntity = gameProxy.getPlayerEntity();
        if (playerEntity == null) {
            // spectating only
            return;
        }

        GameGuiAppState gameGuiAppState = getAppState(GameGuiAppState.class);
        EntityWorld entityWorld = gameProxy.getGame().getWorld();

        int activePlayerEntity = entityWorld.list(TurnComponent.class).get(0);
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
                            setTargetingSpell(spellEntity);
                        } else {
                            setTargetingSpell(null);
                        }
                    });
                })
                .collect(Collectors.toList());
        gameGuiAppState.createSpellButtons(guiSpells);

        gameGuiAppState.createEndTurnButton(this::skipRound);
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
            // spectating only
            return;
        }
        if (isPressed) {
            switch (actionName) {
                case "mouse_left":
                case "mouse_right":
                    Vector3Int clickedPosition = getAppState(MapAppState.class).getHoveredPosition(false);
                    if (clickedPosition != null) {
                        if (targetingSpellEntity != null) {
                            if (containsEntity(gameProxy.getGame().getWorld(), validSpellTargetEntities, clickedPosition.getX(), clickedPosition.getZ())) {
                                gameProxy.requestAction(new CastSpellAction(
                                    clickedPosition.getX(),
                                    clickedPosition.getZ(),
                                    gameProxy.getPlayerEntity().toString(), targetingSpellEntity)
                                );
                            } else {
                                setTargetingSpell(null);
                            }
                        } else {
                            gameProxy.requestAction(new PositionUpdateAction(clickedPosition.getX(), clickedPosition.getZ(), playerEntity.toString()));
                        }
                    }
                    break;
            }
        }
    }

    private boolean containsEntity(EntityWorld entityWorld, List<Integer> entities, int x, int y) {
        return entities.stream().anyMatch(entity -> {
            PositionComponent positionComponent = entityWorld.getComponent(entity, PositionComponent.class);
            return ((positionComponent.getX() == x) && (positionComponent.getY() == y));
        });
    }

    private void setTargetingSpell(Integer spellEntity) {
        targetingSpellEntity = spellEntity;
        if (spellEntity != null) {
            validSpellTargetEntities = RangeUtils.getRange(targetingSpellEntity, gameProxy.getPlayerEntity(), gameProxy.getGame().getWorld());
        } else {
            validSpellTargetEntities.clear();
        }
        getAppState(MapAppState.class).setValidTargetEntities(validSpellTargetEntities);
    }

    public void playAnimation(Animation animation) {
        animation.start();
        playingAnimations.add(animation);
    }

    private void skipRound() {
        gameProxy.requestAction(new SkipRoundAction(gameProxy.getPlayerEntity().toString()));
    }
}
