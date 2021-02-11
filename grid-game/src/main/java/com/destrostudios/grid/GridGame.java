package com.destrostudios.grid;

import com.destrostudios.grid.actions.Action;
import com.destrostudios.grid.actions.ActionDispatcher;
import com.destrostudios.grid.components.*;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.NewEventbus;
import com.destrostudios.grid.eventbus.events.MovementPointsChangedEvent;
import com.destrostudios.grid.eventbus.events.NewEvent;
import com.destrostudios.grid.eventbus.events.PositionChangedEvent;
import com.destrostudios.grid.eventbus.events.RoundSkippedEvent;
import com.destrostudios.grid.eventbus.handler.MovementPointsChangedHandler;
import com.destrostudios.grid.eventbus.handler.NewEventHandler;
import com.destrostudios.grid.eventbus.handler.PositionChangedHandler;
import com.destrostudios.grid.eventbus.handler.RoundSkippedHandler;
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
    private final NewEventbus newEventBus;

    public GridGame() {
        this.world = new EntityWorld();
        this.gamePreferences = new GamePreferences(MAP_X, MAP_Y);
        this.newEventBus = new NewEventbus(() -> world);
    }

    public static void main(String[] args) {
        GridGame gridGame = new GridGame();
        gridGame.initGame(StartGameInfo.getTestGameInfo());
        gridGame.addNewEvent(new PositionChangedEvent(1, new PositionComponent(5, 5)));
        gridGame.triggerAllNewEvents();
        gridGame.addNewEvent(new MovementPointsChangedEvent(1, -1));
        gridGame.triggerAllNewEvents();
        gridGame.addNewEvent(new RoundSkippedEvent(1));
        gridGame.triggerAllNewEvents();
    }

    public void initGame(StartGameInfo startGameInfo) {
        for (PlayerInfo playerInfo : startGameInfo.getTeam1()) {
            addPlayer(playerInfo.getLogin(), 1);
        }
        for (PlayerInfo playerInfo : startGameInfo.getTeam2()) {
            addPlayer(playerInfo.getLogin(), 2);
        }
        addListener();
        initMap();
    }

    public void addNewEvent(NewEvent event) {
        this.newEventBus.addEvent(event);
    }

    public void triggerNewEvent() {
        this.newEventBus.triggerNextEvent();
    }

    public void triggerAllNewEvents() {
        this.newEventBus.triggerAllEvents();
    }

    private void addListener() {
//        this.addListener(new RoundUpdateListener());
//        this.addListener(new PositionUpdateListener());
        newEventBus.addInstantHandler(PositionChangedEvent.class, new PositionChangedHandler(newEventBus));
        newEventBus.addInstantHandler(MovementPointsChangedEvent.class, new MovementPointsChangedHandler(newEventBus));
        newEventBus.addInstantHandler(RoundSkippedEvent.class, new RoundSkippedHandler(newEventBus));
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
        int playerEntity = world.createEntity();
        PositionComponent component = new PositionComponent((int) (gamePreferences.getMapSizeX() * Math.random()),
                (int) (gamePreferences.getMapSizeY() * Math.random()));
        world.addComponent(playerEntity, component);
        world.addComponent(playerEntity, new MovementPointsComponent(MAX_MP));
        world.addComponent(playerEntity, new AttackPointsComponent(MAX_AP));
        world.addComponent(playerEntity, new PlayerComponent(name));
        world.addComponent(playerEntity, new TeamComponent(team));
        world.addComponent(playerEntity, new HealthPointsComponent(MAX_HEALTH));
        world.addComponent(playerEntity, new MaxHealthComponent(MAX_HEALTH));

        if (team == STARTING_TEAM) {
            world.addComponent(playerEntity, new RoundComponent());
        }
    }

    public void intializeGame(String gameState) {
        world.initializeWorld(gameState);
    }


    public <E extends NewEvent> void addListener(Class<E> classz, NewEventHandler<E> handler) {
        this.newEventBus.addInstantHandler(classz, handler);
    }

    public <E extends NewEvent> void removeInstantHandler(NewEventHandler<E> handler) {
        this.newEventBus.removeInstantHandler(handler.getEventClass(), handler);
    }

    public void registerAction(Action action) {
        addNewEvent(ActionDispatcher.dispatchAction(action));
        newEventBus.triggerAllEvents();
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
