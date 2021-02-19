package com.destrostudios.grid;

import com.destrostudios.grid.actions.Action;
import com.destrostudios.grid.actions.ActionDispatcher;
import com.destrostudios.grid.actions.ActionNotAllowedException;
import com.destrostudios.grid.components.Component;
import com.destrostudios.grid.components.character.PlayerComponent;
import com.destrostudios.grid.components.character.RoundComponent;
import com.destrostudios.grid.components.character.TeamComponent;
import com.destrostudios.grid.components.map.PositionComponent;
import com.destrostudios.grid.components.map.StartingFieldComponent;
import com.destrostudios.grid.components.map.WalkableComponent;
import com.destrostudios.grid.components.properties.*;
import com.destrostudios.grid.components.spells.AttackPointCostComponent;
import com.destrostudios.grid.components.spells.MovementPointsCostComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.Eventbus;
import com.destrostudios.grid.eventbus.events.*;
import com.destrostudios.grid.eventbus.handler.*;
import com.destrostudios.grid.eventbus.handler.properties.*;
import com.destrostudios.grid.eventbus.validator.*;
import com.destrostudios.grid.preferences.GamePreferences;
import com.destrostudios.grid.serialization.ComponentsContainerSerializer;
import com.destrostudios.grid.serialization.container.CharacterContainer;
import com.destrostudios.grid.serialization.container.MapContainer;
import com.destrostudios.grid.shared.PlayerInfo;
import com.destrostudios.grid.shared.StartGameInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Getter
public class GridGame {
    // TODO: 09.02.2021 should not be hardcoded
    public final static int MAX_HEALTH = 1000;
    public final static int STARTING_TEAM = 1;
    public final static int MAP_X = 16;
    public final static int MAP_Y = 16;
    public final static int MAX_MP = 15;
    public final static int MAX_AP = 15;
    private final static Logger logger = Logger.getGlobal();
    private final GamePreferences gamePreferences;
    private final EntityWorld world;
    private final Eventbus eventBus;
    private final ActionDispatcher actionDispatcher;

    public GridGame() {
        this.world = new EntityWorld();
        this.gamePreferences = new GamePreferences(MAP_X, MAP_Y);
        this.eventBus = new Eventbus(() -> world);
        this.actionDispatcher = new ActionDispatcher(() -> world);
    }

    public void initGame(StartGameInfo startGameInfo) {
        world.getWorld().putAll(initMap(startGameInfo.getMapName()).getComponents());

        List<Integer> startingEntities = world.list(WalkableComponent.class, StartingFieldComponent.class);
        Random rand = new Random();
        for (PlayerInfo playerInfo : startGameInfo.getTeam1()) {
            initTeam(startingEntities, rand, playerInfo, 1);
        }
        for (PlayerInfo playerInfo : startGameInfo.getTeam2()) {
            initTeam(startingEntities, rand, playerInfo, 2);
        }
        addInstantHandler();
    }

    private void initTeam(List<Integer> startingEntities, Random rand, PlayerInfo playerInfo, int team) {
        int playerEntity = world.createEntity();

        CharacterContainer characterContainer = initCharacter(playerInfo.getLogin());
        addComponentsForCharacter(playerEntity, characterContainer);

        Integer startEntity = startingEntities.remove(rand.nextInt(startingEntities.size()));
        PositionComponent startingPosition = world.getComponent(startEntity, PositionComponent.class);
        if (team == 1) {
            world.addComponent(playerEntity, new RoundComponent());
        }
        world.addComponent(playerEntity, new TeamComponent(team));
        world.addComponent(playerEntity, new PositionComponent(startingPosition.getX(), startingPosition.getY()));
    }

    private void addComponentsForCharacter(int playerEntity, CharacterContainer characterContainer) {
        List<Integer> spells = new ArrayList<>();
        for (Map.Entry<Integer, List<Component>> spellComponentEntry : getSpellComponents(characterContainer).entrySet()) {
            int spell = world.createEntity();
            spellComponentEntry.getValue().forEach(c -> world.addComponent(spell, c));
            spells.add(spell);
        }
        world.addComponent(playerEntity, new SpellsComponent(spells));
        for (Component playerComponent : getPlayerComponentsWithoutSpells(characterContainer)) {
            world.addComponent(playerEntity, playerComponent);
        }
        MaxHealthComponent hpC = world.getComponent(playerEntity, MaxHealthComponent.class);
        world.addComponent(playerEntity, new HealthPointsComponent(hpC.getMaxHealth()));
        MaxAttackPointsComponent apC = world.getComponent(playerEntity, MaxAttackPointsComponent.class);
        world.addComponent(playerEntity, new AttackPointsComponent(apC.getMaxAttackPoints()));
        MaxMovementPointsComponent mpC = world.getComponent(playerEntity, MaxMovementPointsComponent.class);
        world.addComponent(playerEntity, new MovementPointsComponent(mpC.getMaxMovenemtPoints()));
    }

    public List<Component> getPlayerComponentsWithoutSpells(CharacterContainer characterContainer) {
        return characterContainer.getComponents().entrySet().stream()
                .filter(e -> e.getValue().stream().anyMatch(c -> c instanceof PlayerComponent))
                .flatMap(e -> e.getValue().stream())
                .filter(c -> !(c instanceof SpellsComponent))
                .collect(Collectors.toList());
    }

    public Map<Integer, List<Component>> getSpellComponents(CharacterContainer characterContainer) {
        return characterContainer.getComponents().entrySet().stream() // TODO: 14.02.2021 refactoring
                .filter(e -> e.getValue().stream().noneMatch(c -> c instanceof PlayerComponent))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private MapContainer initMap(String mapName) {
        MapContainer destroMap;
        try {
            destroMap = ComponentsContainerSerializer.readSeriazableFromRessources(mapName, MapContainer.class);
        } catch (IOException e) {
            destroMap = new MapContainer();
            logger.warning("Error reading file " + mapName + " from ressources");
        }
        return destroMap;
    }

    private CharacterContainer initCharacter(String characterName) {
        CharacterContainer characterContainer;
        try {
            characterContainer = ComponentsContainerSerializer.readSeriazableFromRessources(characterName, CharacterContainer.class);
        } catch (IOException e) {
            characterContainer = new CharacterContainer();
            logger.warning("Error reading file " + characterName + " from ressources");
        }
        return characterContainer;
    }


    public void registerEvent(Event event) {
        this.eventBus.registerMainEvents(event);
    }

    public boolean triggeredHandlersInQueue() {
        return eventBus.triggeredHandlersInQueue();
    }

    public void triggerNextHandler() {
        eventBus.triggerNextHandler();
    }

    private void addInstantHandler() {
        addInstantHandler(MoveEvent.class, new PositionChangedHandler(eventBus));
        addInstantHandler(PropertiePointsChangedEvent.MovementPointsChangedEvent.class, new MovementPointsChangedHandler());
        addInstantHandler(SimpleUpdateEvent.RoundSkippedEvent.class, new RoundSkippedHandler(eventBus));
        addInstantHandler(PropertiePointsChangedEvent.AttackPointsChangedEvent.class, new AttackPointsChangedHandler());
        addInstantHandler(DamageTakenEvent.class, new DamageTakenHandler(eventBus));
        addInstantHandler(SpellCastedEvent.class, new SpellCastedEventHandler(eventBus));
        addInstantHandler(PropertiePointsChangedEvent.HealthPointsChangedEvent.class, new HealthPointsChangedHandler(eventBus));
        addInstantHandler(PropertiePointsChangedEvent.MaxHealthPointsChangedEvent.class, new MaxHealthPointsChangedHandler());
        addInstantHandler(PropertiePointsChangedEvent.MaxAttackPointsChangedEvent.class, new MaxAttackPointsChangedHandler());
        addInstantHandler(PropertiePointsChangedEvent.MaxMovementPointsChangedEvent.class, new MaxMovementPointsChangedHandler());
        addInstantHandler(BuffAddedEvent.class, new BuffAddedHandler(eventBus));
        addInstantHandler(SimpleUpdateEvent.BuffsUpdateEvent.class, new UpdateBuffsHandler(eventBus));
        addInstantHandler(SimpleUpdateEvent.UpdateCooldownsUpdateEvent.class, new UpdateCooldownsHandler());
        addValidator(MoveEvent.class, new MoveValidator());
        addValidator(SpellCastedEvent.class, new SpellCastedValidator());
        addValidator(SimpleUpdateEvent.RoundSkippedEvent.class, new SkipRoundValidator());
        addValidator(DamageTakenEvent.class, new DamageTakenValidator());
    }

    public void intializeGame(String gameState) {
        world.initializeWorld(gameState);
        addInstantHandler();
    }

    public Map<Integer, List<Component>> getComponents() {
        return world.getWorld();
    }

    public void addValidator(Class<? extends Event> classz, EventValidator<? extends Event> validator) {
        this.eventBus.addEventValidator(classz, validator);
    }

    public void removeValidator(Class<? extends Event> classz, EventValidator<? extends Event> validator) {
        this.eventBus.removeEventValidator(classz, validator);
    }

    public void addInstantHandler(Class<? extends Event> classz, EventHandler<? extends Event> handler) {
        this.eventBus.addInstantHandler(classz, handler);
    }

    public void addPreHandler(Class<? extends Event> classz, EventHandler<? extends Event> handler) {
        this.eventBus.addPreHandler(classz, handler);
    }

    public void addResolvedHandler(Class<? extends Event> classz, EventHandler<? extends Event> handler) {
        this.eventBus.addResolvedHandler(classz, handler);
    }

    public void removePreHandler(Class<? extends Event> eventClass, EventHandler<? extends Event> handler) {
        this.eventBus.removePreHandler(eventClass, handler);
    }

    public void removeResolvedHandler(Class<? extends Event> eventClass, EventHandler<? extends Event> handler) {
        this.eventBus.removeResolvedHandler(eventClass, handler);
    }

    public void registerAction(Action action) {
        try {
            registerEvent(actionDispatcher.dispatchAction(action));
        } catch (ActionNotAllowedException e) {
            logger.log(Level.WARNING, e, () -> "Action not allowed: " + e.getMessage() + "");
        }
    }

    public String getState() {
        try {
            return ComponentsContainerSerializer.getContainerAsJson(world);
        } catch (JsonProcessingException e) {
            logger.log(Level.WARNING, e, () -> "Couldn´t marshal game state!");
        }
        return "";
    }


}
