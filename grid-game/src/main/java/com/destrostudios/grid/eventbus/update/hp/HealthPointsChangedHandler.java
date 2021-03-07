package com.destrostudios.grid.eventbus.update.hp;

import com.destrostudios.grid.components.properties.HealthPointsComponent;
import com.destrostudios.grid.components.properties.MaxHealthComponent;
import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.eventbus.EventHandler;
import com.destrostudios.grid.eventbus.Eventbus;
import com.destrostudios.grid.eventbus.action.gameover.GameOverEvent;
import com.destrostudios.grid.util.GameOverUtils;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class HealthPointsChangedHandler implements EventHandler<HealthPointsChangedEvent> {
    private final Eventbus eventbus;

    @Override
    public void onEvent(HealthPointsChangedEvent event, Supplier<EntityData> entityDataSupplier) {
        EntityData entityData = entityDataSupplier.get();
        MaxHealthComponent maxHealthComponent = entityData.getComponent(event.getEntity(), MaxHealthComponent.class);
        entityData.addComponent(event.getEntity(), new HealthPointsComponent(Math.min(maxHealthComponent.getMaxHealth(), event.getNewPoints())));
        GameOverUtils.GameOverInfo gameOverInfo = GameOverUtils.getGameOverInfo(entityData);
        if (gameOverInfo.isGameIsOver()) {
            eventbus.registerSubEvents(new GameOverEvent(gameOverInfo.getWinningTeamEntity()));
        }
    }

}
