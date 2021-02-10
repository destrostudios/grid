package com.destrostudios.grid;

import com.destrostudios.grid.components.*;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.gamestate.GameStateConverter;
import com.destrostudios.grid.preferences.GamePreferences;
import com.destrostudios.grid.shared.PlayerInfo;
import com.destrostudios.grid.shared.StartGameInfo;
import com.destrostudios.grid.actions.Action;
import com.destrostudios.grid.actions.ActionDispatcher;
import com.destrostudios.grid.eventbus.ComponentEventBus;
import com.destrostudios.grid.eventbus.Listener;
import com.destrostudios.grid.eventbus.listener.PositionUpdateListener;
import com.destrostudios.grid.eventbus.listener.RoundUpdateListener;
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
    public final static int MAX_MP = 10;
    public final static int MAX_AP = 10;

    private final ComponentEventBus ownComponentEventBus;
    private final GamePreferences gamePreferences;
    private final ActionDispatcher actionDispatcher;

    private final EntityWorld world;

    public GridGame() {
        this.world = new EntityWorld();
        this.ownComponentEventBus = new ComponentEventBus();
        this.gamePreferences = new GamePreferences(16, 16);
        this.actionDispatcher = new ActionDispatcher();
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
        world.addComponent(playerEntity, new HealthPointsComponent(MAX_HEALTH));
        world.addComponent(playerEntity, new MaxHealthComponent(MAX_HEALTH));

        if (team == STARTING_TEAM) {
            world.addComponent(playerEntity, new RoundComponent());
        }
    }

    public void intializeGame(String gameState) {
        world.initializeWorld(gameState);
    }

    public void addListener(Listener<? extends Component> listener) {
        actionDispatcher.addListener(listener);
    }

    public void removeListener(Listener<Component> listener) {
        actionDispatcher.removeListener(listener);
    }

    public void registerAction(Action action) {
        actionDispatcher.dispatchAction(action, world);
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
