package com.destrostudios.grid.game;

import com.destrostudios.grid.components.Component;
import com.destrostudios.grid.components.MovingComponent;
import com.destrostudios.grid.components.PlayerComponent;
import com.destrostudios.grid.components.PositionComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.preferences.GamePreferences;
import com.destrostudios.grid.update.ComponentUpdateEvent;
import com.destrostudios.grid.update.listener.ComponentUpdateListener;
import com.destrostudios.grid.update.listener.PositionUpdateListener;
import com.google.common.eventbus.EventBus;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import lombok.Getter;

@Getter
public class Game {
    private final static Logger logger = Logger.getGlobal();

    private final EventBus eventbus = new EventBus();
    private final GamePreferences gamePreferences;

    private EntityWorld world;

    public Game() {
        this.world = new EntityWorld();
        this.gamePreferences = new GamePreferences(20, 20);
    }

    public void fillTestGameData() {
        int playerEntity = world.createEntity();
        world.addComponent(playerEntity, new PositionComponent(0, 0));
        world.addComponent(playerEntity, new MovingComponent());
        world.addComponent(playerEntity, new PlayerComponent("Icecold"));
        this.addListener(new PositionUpdateListener(world));
        update(playerEntity, new PositionComponent(0, 1));
        update(playerEntity, new PositionComponent(2, 1));
    }

    public void intializeGame(String gameState) {
        try {
            world = GameStateConverter.unmarshal(gameState);
        } catch (JAXBException e) {
            logger.log(Level.WARNING, e, () -> "Couldn´t initialize game state!");
        }
    }

    public void addListener(ComponentUpdateListener<?> listener) {
        eventbus.register(listener);
    }

    public void removeListener(ComponentUpdateListener<?> listener) {
        eventbus.unregister(listener);
    }

    public void update(int entity, Component component) {
        eventbus.post(new ComponentUpdateEvent<>(entity, component));
    }

    public <E extends Component> void update(ComponentUpdateEvent<E> component) {
        eventbus.post(component);
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
