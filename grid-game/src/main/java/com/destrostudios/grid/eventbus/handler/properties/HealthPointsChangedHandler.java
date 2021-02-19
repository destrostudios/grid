package com.destrostudios.grid.eventbus.handler.properties;

import com.destrostudios.grid.components.properties.HealthPointsComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.Eventbus;
import com.destrostudios.grid.eventbus.events.GameOverEvent;
import com.destrostudios.grid.eventbus.events.PropertiePointsChangedEvent;
import com.destrostudios.grid.eventbus.handler.EventHandler;
import com.destrostudios.grid.util.GameOverUtils;
import lombok.AllArgsConstructor;

import java.util.function.Supplier;

@AllArgsConstructor
public class HealthPointsChangedHandler implements EventHandler<PropertiePointsChangedEvent.HealthPointsChangedEvent> {
    private final Eventbus eventbus;

    @Override
    public void onEvent(PropertiePointsChangedEvent.HealthPointsChangedEvent event, Supplier<EntityWorld> entityWorldSupplier) {
        entityWorldSupplier.get().addComponent(event.getEntity(), new HealthPointsComponent(event.getNewPoints()));
        GameOverUtils.GameOverInfo gameOverInfo = GameOverUtils.getGameOverInfo(entityWorldSupplier.get());
        if (gameOverInfo.isGameIsOver()) {
            eventbus.registerSubEvents(new GameOverEvent(gameOverInfo.getWinningTeamEntity()));
        }
    }
}
