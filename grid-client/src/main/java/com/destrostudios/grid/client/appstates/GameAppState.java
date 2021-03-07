package com.destrostudios.grid.client.appstates;

import com.destroflyer.jme3.cubes.Vector3Int;
import com.destrostudios.grid.actions.CastSpellAction;
import com.destrostudios.grid.actions.PositionUpdateAction;
import com.destrostudios.grid.actions.SkipRoundAction;
import com.destrostudios.grid.client.ClientApplication;
import com.destrostudios.grid.client.JMonkeyUtil;
import com.destrostudios.grid.client.animations.Animation;
import com.destrostudios.grid.client.animations.AnnouncementAnimation;
import com.destrostudios.grid.client.animations.HealthAnimation;
import com.destrostudios.grid.client.animations.PlayerModelAnimation;
import com.destrostudios.grid.client.animations.WalkAnimation;
import com.destrostudios.grid.client.characters.CastAnimations;
import com.destrostudios.grid.client.characters.ModelAnimationInfo;
import com.destrostudios.grid.client.characters.PlayerVisual;
import com.destrostudios.grid.client.gameproxy.GameProxy;
import com.destrostudios.grid.client.gui.GuiSpell;
import com.destrostudios.grid.components.character.TurnComponent;
import com.destrostudios.grid.components.map.PositionComponent;
import com.destrostudios.grid.components.properties.AttackPointsComponent;
import com.destrostudios.grid.components.properties.HealthPointsComponent;
import com.destrostudios.grid.components.properties.MaxHealthComponent;
import com.destrostudios.grid.components.properties.MovementPointsComponent;
import com.destrostudios.grid.components.properties.NameComponent;
import com.destrostudios.grid.components.properties.SpellsComponent;
import com.destrostudios.grid.components.spells.base.TooltipComponent;
import com.destrostudios.grid.components.spells.limitations.CostComponent;
import com.destrostudios.grid.components.spells.limitations.OnCooldownComponent;
import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.eventbus.Event;
import com.destrostudios.grid.eventbus.EventHandler;
import com.destrostudios.grid.eventbus.action.gameover.GameOverEvent;
import com.destrostudios.grid.eventbus.action.spellcasted.SpellCastedEvent;
import com.destrostudios.grid.eventbus.action.walk.WalkEvent;
import com.destrostudios.grid.eventbus.update.hp.HealthPointsChangedEvent;
import com.destrostudios.grid.eventbus.update.turn.UpdatedTurnEvent;
import com.destrostudios.grid.util.RangeUtils;
import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
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

        gameProxy.addPreHandler(WalkEvent.class, (EventHandler<WalkEvent>) (event, entityDataSupplier) -> {
            PlayerVisual playerVisual = getAppState(MapAppState.class).getPlayerVisual(event.getEntity());
            PositionComponent positionComponent = event.getPositionComponent();
            playAnimation(new WalkAnimation(playerVisual, positionComponent.getX(), positionComponent.getY()));
        });
        gameProxy.addPreHandler(HealthPointsChangedEvent.class, (EventHandler<HealthPointsChangedEvent>) (event, entityDataSupplier) -> {
            EntityData entityData = entityDataSupplier.get();
            int currentHealth = entityData.getComponent(event.getEntity(), HealthPointsComponent.class).getHealth();
            int newHealth = event.getNewPoints();
            if (currentHealth != newHealth) {
                PlayerVisual playerVisual = getAppState(MapAppState.class).getPlayerVisual(event.getEntity());
                playAnimation(new HealthAnimation(playerVisual, newHealth));
            } else {
                System.err.println("HealthPointsChangedEvent, where health didn't change...");
            }
        });
        gameProxy.addPreHandler(SpellCastedEvent.class, (EventHandler<SpellCastedEvent>) (event, entityDataSupplier) -> {
            EntityData entityData = entityDataSupplier.get();
            PlayerVisual playerVisual = getAppState(MapAppState.class).getPlayerVisual(event.getPlayerEntity());
            String spellName = entityData.getComponent(event.getSpell(), NameComponent.class).getName();
            ModelAnimationInfo castAnimation = CastAnimations.get(spellName);
            if (castAnimation != null) {
                lookAt(entityData, event.getPlayerEntity(), playerVisual.getModelObject(), event.getX(), event.getY());
                playAnimation(new PlayerModelAnimation(playerVisual, castAnimation));
            }
        });
        gameProxy.addResolvedHandler(Event.class, (event, entityDataSupplier) -> updateVisuals());
        gameProxy.addResolvedHandler(UpdatedTurnEvent.class, (event, entityDataSupplier) -> {
            EntityData entityData = entityDataSupplier.get();
            int activePlayerEntity = entityData.list(TurnComponent.class).get(0);
            String activePlayerName = entityData.getComponent(activePlayerEntity, NameComponent.class).getName();
            playAnimation(new AnnouncementAnimation(mainApplication, activePlayerName + "s turn"));
        });
        gameProxy.addResolvedHandler(GameOverEvent.class, (EventHandler<GameOverEvent>) (event, entityDataSupplier) -> {
            getAppState(GameGuiAppState.class).onGameOver("Team #" + event.getWinnerTeam());
        });

        mainApplication.getInputManager().addMapping("mouse_left", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        mainApplication.getInputManager().addMapping("mouse_right", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        mainApplication.getInputManager().addListener(this, "mouse_left", "mouse_right");
    }

    @Override
    public void update(float tpf) {
        do {
            while (gameProxy.triggeredHandlersInQueue() && playingAnimations.stream().noneMatch(Animation::isBlocking)) {
                gameProxy.triggerNextHandler();
            }
        } while (gameProxy.applyNextAction());
        for (Animation animation : playingAnimations.toArray(Animation[]::new)) {
            animation.update(tpf);
            if (animation.isFinished()) {
                animation.end();
                playingAnimations.remove(animation);
            }
        }
        super.update(tpf);
    }

    private void updateVisuals() {
        setTargetingSpell(null);
        MapAppState mapAppState = getAppState(MapAppState.class);
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
        EntityData entityData = gameProxy.getGame().getData();

        int activePlayerEntity = entityData.list(TurnComponent.class).get(0);
        String activePlayerName = entityData.getComponent(activePlayerEntity, NameComponent.class).getName();
        int activePlayerMP = entityData.getComponent(activePlayerEntity, MovementPointsComponent.class).getMovementPoints();
        int activePlayerAP = entityData.getComponent(activePlayerEntity, AttackPointsComponent.class).getAttackPoints();
        gameGuiAppState.setActivePlayerName(activePlayerName);
        gameGuiAppState.setActivePlayerMP(activePlayerMP);
        gameGuiAppState.setActivePlayerAP(activePlayerAP);

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
                    CostComponent costComponent = entityData.getComponent(spellEntity, CostComponent.class);
                    boolean isCostPayable = ((costComponent == null) || ((ownPlayerAP >= costComponent.getApCost()) && (ownPlayerMP >= costComponent.getMpCost()) && (ownPlayerCurrentHealth >= costComponent.getHpCost())));
                    boolean isTargeting = Objects.equals(targetingSpellEntity, spellEntity);
                    return new GuiSpell(name, tooltip, remainingCooldown, isCostPayable, isTargeting, () -> {
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
                            if (containsEntity(gameProxy.getGame().getData(), validSpellTargetEntities, clickedPosition.getX(), clickedPosition.getZ())) {
                                gameProxy.requestAction(new CastSpellAction(
                                        clickedPosition.getX(),
                                        clickedPosition.getZ(),
                                        gameProxy.getPlayerEntity().toString(), targetingSpellEntity)
                                );
                            }
                            setTargetingSpell(null);
                        } else {
                            gameProxy.requestAction(new PositionUpdateAction(clickedPosition.getX(), clickedPosition.getZ(), playerEntity.toString()));
                        }
                    }
                    break;
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

    private void setTargetingSpell(Integer spellEntity) {
        targetingSpellEntity = spellEntity;
        if (spellEntity != null) {
            validSpellTargetEntities = RangeUtils.getRange(targetingSpellEntity, gameProxy.getPlayerEntity(), gameProxy.getGame().getData());
        } else {
            validSpellTargetEntities.clear();
        }
        getAppState(MapAppState.class).setValidTargetEntities(validSpellTargetEntities);
        updateGui();
    }

    public void playAnimation(Animation animation) {
        animation.start();
        playingAnimations.add(animation);
    }

    private void skipRound() {
        gameProxy.requestAction(new SkipRoundAction(gameProxy.getPlayerEntity().toString()));
    }
}
