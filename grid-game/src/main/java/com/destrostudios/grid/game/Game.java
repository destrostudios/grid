package com.destrostudios.grid.game;

import com.destrostudios.grid.components.Component;
import com.destrostudios.grid.components.MovingComponent;
import com.destrostudios.grid.components.PlayerComponent;
import com.destrostudios.grid.components.PositionComponent;
import com.destrostudios.grid.entities.EntityWorld;

import com.destrostudios.grid.update.ComponentUpdateEvent;
import com.destrostudios.grid.update.listener.ComponentUpdateListener;
import com.destrostudios.grid.update.listener.PositionUpdateListener;
import com.destrostudios.grid.preferences.GamePreferences;
import com.destrostudios.grid.systems.ComponentSystemController;
import com.google.common.eventbus.EventBus;
import lombok.Getter;


import javax.swing.*;
import java.util.logging.Logger;

@Getter
public class Game {
    private final static Logger logger = Logger.getGlobal();
    private final static EventBus eventbus = new EventBus();

    private final EntityWorld world;

    private final ComponentSystemController systemController;
    private final GamePreferences gamePreferences;

    public Game() {
        this.world = new EntityWorld();
        this.systemController = new ComponentSystemController();
        this.gamePreferences = new GamePreferences();
//        fillTestGameData();
    }

    public static void main(String[] args) {
        Game game = new Game();
    }

    public void fillTestGameData() {
        int playerEntity = world.createEntity();
        world.addComponent(playerEntity, new PositionComponent(0, 0));
        world.addComponent(playerEntity, new MovingComponent());
        world.addComponent(playerEntity, new PlayerComponent("Icecold"));
        this.addListener(new PositionUpdateListener(world));

//        update(0, new PositionComponent(0, 1));
//        update(0, new PositionComponent(2, 1));
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

}
