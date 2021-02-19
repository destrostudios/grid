package com.destrostudios.grid.eventbus.handler.properties;

import com.destrostudios.grid.components.properties.HealthPointsComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.Eventbus;
import com.destrostudios.grid.eventbus.events.GameOverEvent;
import com.destrostudios.grid.eventbus.events.properties.HealthPointsChangedEvent;
import com.destrostudios.grid.eventbus.handler.EventHandler;
import com.destrostudios.grid.util.GameOverUtils;
import lombok.AllArgsConstructor;

import java.util.function.Supplier;

@AllArgsConstructor
public class HealthPointsChangedHandler implements EventHandler<HealthPointsChangedEvent> {
    private final Eventbus eventbus;

    @Override
    public void onEvent(HealthPointsChangedEvent event, Supplier<EntityWorld> entityWorldSupplier) {
        EntityWorld entityWorld = entityWorldSupplier.get();
        entityWorld.remove(event.getEntity(), HealthPointsComponent.class);
        int newHealthPoints = event.getNewHealthPoints();
        entityWorld.addComponent(event.getEntity(), new HealthPointsComponent(newHealthPoints));

        GameOverUtils.GameOverInfo gameOverInfo = GameOverUtils.getGameOverInfo(entityWorld);
        if (gameOverInfo.isGameIsOver()) {
            eventbus.registerSubEvents(new GameOverEvent(gameOverInfo.getWinningTeamEntity()));
        }
    }
}
