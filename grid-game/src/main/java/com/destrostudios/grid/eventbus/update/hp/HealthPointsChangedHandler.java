package com.destrostudios.grid.eventbus.update.hp;

import com.destrostudios.grid.components.properties.HealthPointsComponent;
import com.destrostudios.grid.components.properties.MaxHealthComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.EventHandler;
import com.destrostudios.grid.eventbus.Eventbus;
import com.destrostudios.grid.eventbus.action.gameover.GameOverEvent;
import com.destrostudios.grid.util.GameOverUtils;
import lombok.AllArgsConstructor;

import java.util.function.Supplier;

@AllArgsConstructor
public class HealthPointsChangedHandler implements EventHandler<HealthPointsChangedEvent> {
    private final Eventbus eventbus;

    @Override
    public void onEvent(HealthPointsChangedEvent event, Supplier<EntityWorld> entityWorldSupplier) {
        EntityWorld entityWorld = entityWorldSupplier.get();
        MaxHealthComponent maxHealthComponent = entityWorld.getComponent(event.getEntity(), MaxHealthComponent.class);
        entityWorld.addComponent(event.getEntity(), new HealthPointsComponent(Math.min(maxHealthComponent.getMaxHealth(), event.getNewPoints())));
        GameOverUtils.GameOverInfo gameOverInfo = GameOverUtils.getGameOverInfo(entityWorld);
        if (gameOverInfo.isGameIsOver()) {
            eventbus.registerSubEvents(new GameOverEvent(gameOverInfo.getWinningTeamEntity()));
        }
    }

}
