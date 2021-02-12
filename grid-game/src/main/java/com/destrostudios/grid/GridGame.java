package com.destrostudios.grid;

import com.destrostudios.grid.actions.Action;
import com.destrostudios.grid.actions.ActionDispatcher;
import com.destrostudios.grid.actions.ActionNotAllowedException;
import com.destrostudios.grid.components.*;
import com.destrostudios.grid.components.spells.AttackPointCostComponent;
import com.destrostudios.grid.components.spells.DamageComponent;
import com.destrostudios.grid.components.spells.SpellComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.Eventbus;
import com.destrostudios.grid.eventbus.events.*;
import com.destrostudios.grid.eventbus.handler.*;
import com.destrostudios.grid.gamestate.GameStateConverter;
import com.destrostudios.grid.preferences.GamePreferences;
import com.destrostudios.grid.shared.PlayerInfo;
import com.destrostudios.grid.shared.StartGameInfo;
import lombok.Getter;

import javax.xml.bind.JAXBException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
public class GridGame {
    private final static Logger logger = Logger.getGlobal();

    // TODO: 09.02.2021 should not be hardcoded
    private final static int MAX_HEALTH = 100;
    private final static int STARTING_TEAM = 1;
    private final static int MAP_X = 16;
    private final static int MAP_Y = 16;
    public final static int MAX_MP = 10;
    public final static int MAX_AP = 10;

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
        for (PlayerInfo playerInfo : startGameInfo.getTeam1()) {
            addPlayer(playerInfo.getLogin(), 1);
        }
        for (PlayerInfo playerInfo : startGameInfo.getTeam2()) {
            addPlayer(playerInfo.getLogin(), 2);
        }
        addInstantHandler();
        initMap();
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
//        this.addListener(new RoundUpdateListener());
//        this.addListener(new PositionUpdateListener());
        addInstantHandler(PositionChangedEvent.class, new PositionChangedHandler(eventBus));
        addInstantHandler(MovementPointsChangedEvent.class, new MovementPointsChangedHandler(eventBus));
        addInstantHandler(RoundSkippedEvent.class, new RoundSkippedHandler(eventBus));
        addInstantHandler(AttackPointsChangedEvent.class, new AttackPointsChangedHandler());
        addInstantHandler(DamageTakenEvent.class, new DamageTakenHandler());
        addInstantHandler(SpellCastedEvent.class, new SpellCastedEventHandler(eventBus));
    }

    private void initMap() {
        for (int x = 0; x < MAP_X; x++) {
            for (int y = 0; y < MAP_Y; y++) {
                int fieldComponent = world.createEntity();
                int finalX = x;
                int finalY = y;
                boolean isFree = world.listComponents(PositionComponent.class).stream()
                        .noneMatch(positionComponent -> positionComponent.getX() == finalX && positionComponent.getY() == finalY);
                if (!isFree || Math.random() > 0.2) {
                    // add walkable component
                    world.addComponent(fieldComponent, new WalkableComponent());
                    world.addComponent(fieldComponent, new PositionComponent(x, y));

                } else if (Math.random() < 0.1) {
                    // add tree component
                    world.addComponent(fieldComponent, new WalkableComponent());
                    world.addComponent(fieldComponent, new PositionComponent(x, y));
                    int treeComponent = world.createEntity();
                    world.addComponent(treeComponent, new PositionComponent(x, y));
                    world.addComponent(treeComponent, new TreeComponent());
                }
            }
        }
    }

    private void addPlayer(String name, int team) {
        int spell = world.createEntity();
        addSpellComponents(team, spell);

        int playerEntity = world.createEntity();
        PositionComponent component = new PositionComponent((int) (gamePreferences.getMapSizeX() * Math.random()),
                (int) (gamePreferences.getMapSizeY() * Math.random()));
        world.addComponent(playerEntity, component);
        world.addComponent(playerEntity, new MovementPointsComponent(MAX_MP));
        world.addComponent(playerEntity, new AttackPointsComponent(MAX_AP));
        world.addComponent(playerEntity, new PlayerComponent());
        world.addComponent(playerEntity, new NameComponent(name));
        world.addComponent(playerEntity, new TeamComponent(team));
        world.addComponent(playerEntity, new HealthPointsComponent(MAX_HEALTH));
        world.addComponent(playerEntity, new MaxHealthComponent(MAX_HEALTH));
        world.addComponent(playerEntity, new SpellComponent(spell));

        if (team == STARTING_TEAM) {
            world.addComponent(playerEntity, new RoundComponent());
        }
    }

    private void addSpellComponents(int team, int spell) {
        world.addComponent(spell, new AttackPointCostComponent(Math.min((int) (Math.random() * 10), 1)));
        world.addComponent(spell, new DamageComponent((int) (Math.random() * 50) + 1));
        world.addComponent(spell, new NameComponent(team == 1 ? "Destrobomb" : "Etherbeam"));
    }

    public void intializeGame(String gameState) {
        world.initializeWorld(gameState);
        addInstantHandler();
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

    public void removeInstantHandler(Class<? extends Event> eventClass, EventHandler<? extends Event> handler) {
        this.eventBus.removeInstantHandler(eventClass, handler);
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
            return GameStateConverter.marshal(world);
        } catch (JAXBException e) {
            logger.log(Level.WARNING, e, () -> "Couldn´t marshal game state!");
        }
        return "";
    }

}
