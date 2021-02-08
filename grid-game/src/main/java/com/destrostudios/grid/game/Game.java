package com.destrostudios.grid.game;

import com.destrostudios.grid.components.*;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.game.gamestate.GameStateConverter;
import com.destrostudios.grid.preferences.GamePreferences;
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

    private final ComponentEventBus ownComponentEventBus;
    private final GamePreferences gamePreferences;

    private EntityWorld world;

    public Game() {
        this.world = new EntityWorld();
        this.ownComponentEventBus = new ComponentEventBus();
        this.gamePreferences = new GamePreferences(20, 20);
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.initGame();
        System.out.println(game.getState());
        game.intializeGame(game.getState());
        game.update(new ComponentUpdateEvent<>(1, new RoundComponent()));
        game.update(new ComponentUpdateEvent<>(1, new PositionComponent(3, 3)));
        System.out.println(game.getState());

    }

    public void initGame() {
        addPlayer("destroflyer", 0);
        addPlayer("etherblood", 1);
        this.addListener(new RoundUpdateListener());
        this.addListener(new PositionUpdateListener());
    }


    public void addPlayer(String name, int team) {
        int playerEntity = world.createEntity();
        world.addComponent(playerEntity, new PositionComponent((int) (20 * Math.random()), (int) (20 * Math.random())));
        world.addComponent(playerEntity, new MovingComponent());
        world.addComponent(playerEntity, new PlayerComponent(name));
        world.addComponent(playerEntity, new TeamComponent(team));
        if (team == 0) {
            world.addComponent(playerEntity, new RoundComponent());
        }
    }

    public void intializeGame(String gameState) {
        try {
            world = GameStateConverter.unmarshal(gameState);
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
