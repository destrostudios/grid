package com.destrostudios.grid.game;

import com.destrostudios.grid.components.*;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.game.gamestate.GameStateConverter;
import com.destrostudios.grid.preferences.GamePreferences;
import com.destrostudios.grid.shared.PlayerInfo;
import com.destrostudios.grid.shared.StartGameInfo;
import com.destrostudios.grid.update.listener.*;
import com.destrostudios.grid.update.eventbus.ComponentUpdateEvent;
import com.destrostudios.grid.update.eventbus.ComponentEventBus;
import com.destrostudios.grid.update.eventbus.Listener;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;

import lombok.Getter;

@Getter
public class Game {
    private final static Logger logger = Logger.getGlobal();

    // TODO: 09.02.2021 should not be hardcoded
    private final static int MAX_HEALT = 100;
    private final static int STARTING_TEAM = 1;
    public final static int MAX_MP = 10;
    public final static int MAX_AP = 10;

    private final ComponentEventBus ownComponentEventBus;
    private final GamePreferences gamePreferences;

    private EntityWorld world;

    public Game() {
        this.world = new EntityWorld();
        this.ownComponentEventBus = new ComponentEventBus();
        this.gamePreferences = new GamePreferences(16, 16);
    }

    public void initGame(StartGameInfo startGameInfo) {
        for (PlayerInfo playerInfo : startGameInfo.getTeam1()) {
            addPlayer(playerInfo.getLogin(), 1);
        }
        for (PlayerInfo playerInfo : startGameInfo.getTeam2()) {
            addPlayer(playerInfo.getLogin(), 2);
        }

        addListener();
    }

    private void addListener() {
        this.addListener(new RoundUpdateListener());
        this.addListener(new PositionUpdateListener());
    }

    private void addPlayer(String name, int team) {
        int playerEntity = world.createEntity();
        world.addComponent(playerEntity, new PositionComponent((int) (gamePreferences.getMapSizeX() * Math.random()), (int) (gamePreferences.getMapSizeY() * Math.random())));
        world.addComponent(playerEntity, new MovementPointsComponent(MAX_MP));
        world.addComponent(playerEntity, new AttackPointsComponent(MAX_AP));
        world.addComponent(playerEntity, new PlayerComponent(name));
        world.addComponent(playerEntity, new TeamComponent(team));
        world.addComponent(playerEntity, new HealthPointsComponent(MAX_HEALT));
        world.addComponent(playerEntity, new MaxHealthComponent(MAX_HEALT));

        if (team == STARTING_TEAM) {
            world.addComponent(playerEntity, new RoundComponent());
        }
    }

    public String componentToXml(Component component) {
        try {
            return GameStateConverter.marshal(component);
        } catch (JAXBException e) {
            logger.log(Level.WARNING, e, () -> "Couldn´t marshal component!");
        }
        return "";
    }

    public Component xmlToComponent(String xml) {
        try {
            return GameStateConverter.unmarshalComponent(xml);
        } catch (JAXBException e) {
            logger.log(Level.WARNING, e, () -> "Couldn´t unmarshal component!");
        }
        return null;
    }

    public void intializeGame(String gameState) {
        try {
            world = GameStateConverter.unmarshal(gameState);
            addListener();
        } catch (JAXBException e) {
            logger.log(Level.WARNING, e, () -> "Couldn´t initialize game state!");
        }
    }

    public void addListener(Listener<? extends Component> listener) {
        ownComponentEventBus.register(listener);
    }

    public void removeOwnListener(Listener<Component> listener) {
        ownComponentEventBus.unregister(listener);
    }

    public void update(int entity, Component component) {
        ownComponentEventBus.publish(new ComponentUpdateEvent<>(entity, component), world);
    }

    public void update(ComponentUpdateEvent<? extends Component> component) {
        ownComponentEventBus.publish(component, world);
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
