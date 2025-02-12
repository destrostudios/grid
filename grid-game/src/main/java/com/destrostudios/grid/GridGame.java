package com.destrostudios.grid;

import com.destrostudios.grid.actions.Action;
import com.destrostudios.grid.actions.ActionDispatcher;
import com.destrostudios.grid.actions.ActionNotAllowedException;
import com.destrostudios.grid.components.Component;
import com.destrostudios.grid.components.character.ActiveTurnComponent;
import com.destrostudios.grid.components.character.NextTurnComponent;
import com.destrostudios.grid.components.character.PlayerComponent;
import com.destrostudios.grid.components.character.TeamComponent;
import com.destrostudios.grid.components.map.PositionComponent;
import com.destrostudios.grid.components.map.StartingFieldComponent;
import com.destrostudios.grid.components.map.TargetableComponent;
import com.destrostudios.grid.components.map.WalkableComponent;
import com.destrostudios.grid.components.properties.*;
import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.Event;
import com.destrostudios.grid.eventbus.EventHandler;
import com.destrostudios.grid.eventbus.EventValidator;
import com.destrostudios.grid.eventbus.Eventbus;
import com.destrostudios.grid.eventbus.action.beginturn.BeginTurnEvent;
import com.destrostudios.grid.eventbus.action.beginturn.BeginTurnHandler;
import com.destrostudios.grid.eventbus.action.damagetaken.DamageTakenEvent;
import com.destrostudios.grid.eventbus.action.damagetaken.DamageTakenHandler;
import com.destrostudios.grid.eventbus.action.damagetaken.DamageTakenValidator;
import com.destrostudios.grid.eventbus.action.die.DieEvent;
import com.destrostudios.grid.eventbus.action.die.DieEventHandler;
import com.destrostudios.grid.eventbus.action.die.DieEventValidator;
import com.destrostudios.grid.eventbus.action.displace.PushEvent;
import com.destrostudios.grid.eventbus.action.displace.PushHandler;
import com.destrostudios.grid.eventbus.action.displace.PushValidator;
import com.destrostudios.grid.eventbus.action.endturn.EndTurnEvent;
import com.destrostudios.grid.eventbus.action.endturn.EndTurnHandler;
import com.destrostudios.grid.eventbus.action.healreceived.HealReceivedEvent;
import com.destrostudios.grid.eventbus.action.healreceived.HealReceivedHandler;
import com.destrostudios.grid.eventbus.action.healreceived.HealReceivedValidator;
import com.destrostudios.grid.eventbus.action.move.MoveEvent;
import com.destrostudios.grid.eventbus.action.move.MoveHandler;
import com.destrostudios.grid.eventbus.action.move.MoveValidator;
import com.destrostudios.grid.eventbus.action.spawn.SummonCastEvent;
import com.destrostudios.grid.eventbus.action.spawn.SummonCastHandler;
import com.destrostudios.grid.eventbus.action.spellcasted.SpellCastedEvent;
import com.destrostudios.grid.eventbus.action.spellcasted.SpellCastedEventHandler;
import com.destrostudios.grid.eventbus.action.spellcasted.SpellCastedValidator;
import com.destrostudios.grid.eventbus.action.swap.SwapEvent;
import com.destrostudios.grid.eventbus.action.swap.SwapEventHandler;
import com.destrostudios.grid.eventbus.action.swap.SwapEventValidator;
import com.destrostudios.grid.eventbus.action.walk.WalkEvent;
import com.destrostudios.grid.eventbus.action.walk.WalkHandler;
import com.destrostudios.grid.eventbus.action.walk.WalkValidator;
import com.destrostudios.grid.eventbus.add.playerbuff.PlayerBuffAddedEvent;
import com.destrostudios.grid.eventbus.add.playerbuff.PlayerBuffAddedHandler;
import com.destrostudios.grid.eventbus.add.poison.StatsPerTurnEvent;
import com.destrostudios.grid.eventbus.add.poison.StatsPerTurnHandler;
import com.destrostudios.grid.eventbus.add.spellbuff.SpellBuffAddedEvent;
import com.destrostudios.grid.eventbus.add.spellbuff.SpellBuffAddedHandler;
import com.destrostudios.grid.eventbus.add.summon.SummonBuffEvent;
import com.destrostudios.grid.eventbus.add.summon.SummonBuffEventHandler;
import com.destrostudios.grid.eventbus.update.ap.AttackPointsChangedEvent;
import com.destrostudios.grid.eventbus.update.ap.AttackPointsChangedHandler;
import com.destrostudios.grid.eventbus.update.hp.HealthPointsChangedEvent;
import com.destrostudios.grid.eventbus.update.hp.HealthPointsChangedHandler;
import com.destrostudios.grid.eventbus.update.lifespan.LifespanUpdateEvent;
import com.destrostudios.grid.eventbus.update.lifespan.LifespanUpdateHandler;
import com.destrostudios.grid.eventbus.update.lifespan.LifespanUpdateValidator;
import com.destrostudios.grid.eventbus.update.maxap.MaxAttackPointsChangedEvent;
import com.destrostudios.grid.eventbus.update.maxap.MaxAttackPointsChangedHandler;
import com.destrostudios.grid.eventbus.update.maxhp.MaxHealthPointsChangedEvent;
import com.destrostudios.grid.eventbus.update.maxhp.MaxHealthPointsChangedHandler;
import com.destrostudios.grid.eventbus.update.maxmp.MaxMovementPointsChangedEvent;
import com.destrostudios.grid.eventbus.update.maxmp.MaxMovementPointsChangedHandler;
import com.destrostudios.grid.eventbus.update.mp.MovementPointsChangedEvent;
import com.destrostudios.grid.eventbus.update.mp.MovementPointsChangedHandler;
import com.destrostudios.grid.eventbus.update.playerenchantments.UpdatePlayerEnchantmentsEvent;
import com.destrostudios.grid.eventbus.update.playerenchantments.UpdatePlayerEnchantmentsHandler;
import com.destrostudios.grid.eventbus.update.position.PositionUpdateEvent;
import com.destrostudios.grid.eventbus.update.position.PositionUpdateHandler;
import com.destrostudios.grid.eventbus.update.spells.UpdateSpellsEvent;
import com.destrostudios.grid.eventbus.update.spells.UpdateSpellsHandler;
import com.destrostudios.grid.eventbus.update.statsperturn.UpdateStatsPerTurnEvent;
import com.destrostudios.grid.eventbus.update.statsperturn.UpdateStatsPerTurnHandler;
import com.destrostudios.grid.eventbus.update.statsperturn.UpdateStatsPerTurnValidator;
import com.destrostudios.grid.eventbus.update.turn.UpdatedTurnEvent;
import com.destrostudios.grid.eventbus.update.turn.UpdatedTurnHandler;
import com.destrostudios.grid.eventbus.update.turn.UpdatedTurnValidator;
import com.destrostudios.grid.preferences.GamePreferences;
import com.destrostudios.grid.random.ImmutableRandomProxy;
import com.destrostudios.grid.random.RandomProxy;
import com.destrostudios.grid.serialization.ComponentsContainerSerializer;
import com.destrostudios.grid.serialization.container.CharacterContainer;
import com.destrostudios.grid.serialization.container.MapContainer;
import com.destrostudios.grid.shared.PlayerInfo;
import com.destrostudios.grid.shared.StartGameInfo;
import com.destrostudios.grid.util.GameOverInfo;
import com.destrostudios.grid.util.TooltipsGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.SneakyThrows;

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
  public static final int MAX_HEALTH = 1000;
  public static final int STARTING_TEAM = 1;
  public static final int MAP_X = 16;
  public static final int MAP_Y = 16;
  public static final int MAX_MP = 15;
  public static final int MAX_AP = 15;
  private static final Logger logger = Logger.getGlobal();
  private final GamePreferences gamePreferences;
  private final GameOverInfo gameOverInfo;
  private final EntityWorld world;
  private final Eventbus eventBus;
  private final ActionDispatcher actionDispatcher;
  private final RandomProxy random;

  public GridGame() {
    this(new Random());
  }

  public GridGame(Random random) {
    this(new ImmutableRandomProxy(random::nextInt));
  }

  public GridGame(RandomProxy random) {
    this.world = new EntityWorld();
    this.gamePreferences = new GamePreferences(MAP_X, MAP_Y);
    this.eventBus = new Eventbus(() -> world);
    this.actionDispatcher = new ActionDispatcher(() -> world);
    this.random = random;
    this.gameOverInfo = new GameOverInfo(world, eventBus);
  }

  public EntityData getData() {
    return world;
  }

  public void initGame(StartGameInfo startGameInfo) {
    Map<Integer, List<Component>> components = initMap(startGameInfo.getMapName()).getComponents();
    for (Map.Entry<Integer, List<Component>> entry : components.entrySet()) {
      for (Component component : entry.getValue()) {
        world.addComponent(entry.getKey(), component);
      }
    }
    world.setNextEntity(components.keySet().stream().mapToInt(x -> x).max().orElse(0) + 1);

    List<PositionComponent> startPositionsTeam1 =
        world.list(WalkableComponent.class, StartingFieldComponent.class).stream()
            .filter(e -> world.getComponent(e, StartingFieldComponent.class).getTeam() == 0)
            .map(e -> world.getComponent(e, PositionComponent.class))
            .collect(Collectors.toList());
    List<PositionComponent> startPositionsTeam2 =
        world.list(WalkableComponent.class, StartingFieldComponent.class).stream()
            .filter(e -> world.getComponent(e, StartingFieldComponent.class).getTeam() == 1)
            .map(e -> world.getComponent(e, PositionComponent.class))
            .collect(Collectors.toList());

    List<PlayerInfo> team1 = startGameInfo.getTeam1();
    List<PlayerInfo> team2 = startGameInfo.getTeam2();
    List<PlayerInfo> turnOrder = initTurnOrder(team1, team2);

    int firstPlayer =
        initPlayer(
            startPositionsTeam1.remove(random.nextInt(0, startPositionsTeam1.size())),
            team1.get(0),
            1);
    int lastPlayer = firstPlayer;
    for (int i = 1; i < turnOrder.size(); i++) {
      PlayerInfo playerInfo = turnOrder.get(i);
      int playerEntity =
          initPlayer(
              startPositionsTeam2.remove(random.nextInt(0, startPositionsTeam2.size())),
              playerInfo,
              team1.contains(playerInfo) ? 1 : 2);

      world.addComponent(lastPlayer, new NextTurnComponent(playerEntity));
      lastPlayer = playerEntity;
    }
    world.addComponent(lastPlayer, new NextTurnComponent(firstPlayer));
    world.addComponent(firstPlayer, new ActiveTurnComponent());

    addInstantHandler();
  }

  private List<PlayerInfo> initTurnOrder(List<PlayerInfo> team1, List<PlayerInfo> team2) {
    List<PlayerInfo> turnOrder = new ArrayList<>();
    for (int i = 0; i < Math.max(team1.size(), team2.size()); i++) {
      if (i < team1.size()) {
        turnOrder.add(team1.get(i));
      }
      if (i < team2.size()) {
        turnOrder.add(team2.get(i));
      }
    }
    return turnOrder;
  }

  private int initPlayer(PositionComponent startingPosition, PlayerInfo playerInfo, int team) {
    int playerEntity = world.createEntity();

    CharacterContainer characterContainer = initCharacter(playerInfo);
    addComponentsForCharacter(playerEntity, characterContainer, playerInfo.getLogin());
    world.addComponent(playerEntity, new TeamComponent(team));
    world.addComponent(
        playerEntity, new PositionComponent(startingPosition.getX(), startingPosition.getY()));
    return playerEntity;
  }

  private void addComponentsForCharacter(
      int playerEntity, CharacterContainer characterContainer, String login) {
    List<Integer> spells = new ArrayList<>();
    for (Map.Entry<Integer, List<Component>> spellComponentEntry :
        getSpellComponents(characterContainer).entrySet()) {
      int spell = world.createEntity();
      spellComponentEntry.getValue().forEach(c -> world.addComponent(spell, c));
      spells.add(spell);
      world.addComponent(spell, TooltipsGenerator.generateTooltip(world, spell));
    }
    world.addComponent(playerEntity, new SpellsComponent(spells));
    for (Component playerComponent : getPlayerComponentsWithoutSpells(characterContainer)) {
      world.addComponent(playerEntity, playerComponent);
    }
    world.addComponent(playerEntity, new NameComponent(login));
    MaxHealthComponent hpC = world.getComponent(playerEntity, MaxHealthComponent.class);
    world.addComponent(playerEntity, new HealthPointsComponent(hpC.getMaxHealth()));
    MaxAttackPointsComponent apC = world.getComponent(playerEntity, MaxAttackPointsComponent.class);
    world.addComponent(playerEntity, new AttackPointsComponent(apC.getMaxAttackPoints()));
    MaxMovementPointsComponent mpC =
        world.getComponent(playerEntity, MaxMovementPointsComponent.class);
    world.addComponent(playerEntity, new MovementPointsComponent(mpC.getMaxMovementPoints()));
    world.addComponent(playerEntity, new IsAliveComponent());
    world.addComponent(playerEntity, new TargetableComponent());
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

  @SneakyThrows
  private MapContainer initMap(String mapName) {
    return ComponentsContainerSerializer.readSeriazableFromRessources(mapName, MapContainer.class);
  }

  @SneakyThrows
  private CharacterContainer initCharacter(PlayerInfo playerInfo) {
    return ComponentsContainerSerializer.readSeriazableFromRessources(
        playerInfo.getCharacterName(), CharacterContainer.class);
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
    addInstantHandler(WalkEvent.class, new WalkHandler(eventBus));
    addInstantHandler(MovementPointsChangedEvent.class, new MovementPointsChangedHandler());
    addInstantHandler(UpdatedTurnEvent.class, new UpdatedTurnHandler(eventBus));
    addInstantHandler(AttackPointsChangedEvent.class, new AttackPointsChangedHandler());
    addInstantHandler(DamageTakenEvent.class, new DamageTakenHandler(eventBus));
    addInstantHandler(SpellCastedEvent.class, new SpellCastedEventHandler(eventBus, random));
    addInstantHandler(HealthPointsChangedEvent.class, new HealthPointsChangedHandler(eventBus));
    addInstantHandler(HealReceivedEvent.class, new HealReceivedHandler(eventBus));
    addInstantHandler(MaxHealthPointsChangedEvent.class, new MaxHealthPointsChangedHandler());
    addInstantHandler(MaxAttackPointsChangedEvent.class, new MaxAttackPointsChangedHandler());
    addInstantHandler(MaxMovementPointsChangedEvent.class, new MaxMovementPointsChangedHandler());
    addInstantHandler(PlayerBuffAddedEvent.class, new PlayerBuffAddedHandler(eventBus));
    addInstantHandler(StatsPerTurnEvent.class, new StatsPerTurnHandler());
    addInstantHandler(PositionUpdateEvent.class, new PositionUpdateHandler(eventBus));
    addInstantHandler(
        UpdateStatsPerTurnEvent.class, new UpdateStatsPerTurnHandler(eventBus, random));
    addInstantHandler(
        UpdatePlayerEnchantmentsEvent.class, new UpdatePlayerEnchantmentsHandler(eventBus));
    addInstantHandler(MoveEvent.class, new MoveHandler(eventBus));
    addInstantHandler(EndTurnEvent.class, new EndTurnHandler(eventBus));
    addInstantHandler(BeginTurnEvent.class, new BeginTurnHandler(eventBus));
    addInstantHandler(PushEvent.class, new PushHandler(eventBus));
    addInstantHandler(UpdateSpellsEvent.class, new UpdateSpellsHandler());
    addInstantHandler(LifespanUpdateEvent.class, new LifespanUpdateHandler());
    addInstantHandler(SpellBuffAddedEvent.class, new SpellBuffAddedHandler(eventBus));
    addInstantHandler(SwapEvent.class, new SwapEventHandler());
    addInstantHandler(DieEvent.class, new DieEventHandler(gameOverInfo, eventBus));
    addInstantHandler(SummonCastEvent.class, new SummonCastHandler());
    addInstantHandler(SummonBuffEvent.class, new SummonBuffEventHandler());

    addValidator(WalkEvent.class, new WalkValidator());
    addValidator(SpellCastedEvent.class, new SpellCastedValidator());
    addValidator(UpdatedTurnEvent.class, new UpdatedTurnValidator());
    addValidator(DamageTakenEvent.class, new DamageTakenValidator());
    addValidator(UpdateStatsPerTurnEvent.class, new UpdateStatsPerTurnValidator());
    addValidator(MoveEvent.class, new MoveValidator());
    addValidator(PushEvent.class, new PushValidator());
    addValidator(SwapEvent.class, new SwapEventValidator());
    addValidator(LifespanUpdateEvent.class, new LifespanUpdateValidator());
    addValidator(DieEvent.class, new DieEventValidator());
    addValidator(HealReceivedEvent.class, new HealReceivedValidator());
  }

  public void initializeGame(String gameState) {
    world.initializeWorld(gameState);
    addInstantHandler();
  }

  public void initializeGame(EntityWorld worldToCopy) {
    world.initializeWorld(worldToCopy);
    addInstantHandler();
  }

  public void addValidator(
      Class<? extends Event> classz, EventValidator<? extends Event> validator) {
    this.eventBus.addEventValidator(classz, validator);
  }

  public void removeValidator(
      Class<? extends Event> classz, EventValidator<? extends Event> validator) {
    this.eventBus.removeEventValidator(classz, validator);
  }

  public void addInstantHandler(
      Class<? extends Event> classz, EventHandler<? extends Event> handler) {
    this.eventBus.addInstantHandler(classz, handler);
  }

  public void addPreHandler(Class<? extends Event> classz, EventHandler<? extends Event> handler) {
    this.eventBus.addPreHandler(classz, handler);
  }

  public void addResolvedHandler(
      Class<? extends Event> classz, EventHandler<? extends Event> handler) {
    this.eventBus.addResolvedHandler(classz, handler);
  }

  public void removePreHandler(
      Class<? extends Event> eventClass, EventHandler<? extends Event> handler) {
    this.eventBus.removePreHandler(eventClass, handler);
  }

  public void removeResolvedHandler(
      Class<? extends Event> eventClass, EventHandler<? extends Event> handler) {
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
