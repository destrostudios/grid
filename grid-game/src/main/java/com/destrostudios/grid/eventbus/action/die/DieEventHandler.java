package com.destrostudios.grid.eventbus.action.die;

import com.destrostudios.grid.components.character.ActiveTurnComponent;
import com.destrostudios.grid.components.character.NextTurnComponent;
import com.destrostudios.grid.components.map.ObstacleComponent;
import com.destrostudios.grid.components.properties.IsAliveComponent;
import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.eventbus.Event;
import com.destrostudios.grid.eventbus.EventHandler;
import com.destrostudios.grid.eventbus.Eventbus;
import com.destrostudios.grid.eventbus.action.beginturn.BeginTurnEvent;
import com.destrostudios.grid.eventbus.action.endturn.EndTurnEvent;
import com.destrostudios.grid.util.GameOverInfo;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.function.Supplier;

@AllArgsConstructor
public class DieEventHandler implements EventHandler<DieEvent> {
    private final GameOverInfo gameOverInfo;
    private final Eventbus eventbus;

    @Override
    public void onEvent(DieEvent event, Supplier<EntityData> entityDataSupplier) {
        EntityData entityData = entityDataSupplier.get();
        entityData.remove(event.getEntity(), ObstacleComponent.class);
        entityData.remove(event.getEntity(), IsAliveComponent.class);

        gameOverInfo.checkGameOverStatus();
        updateNextTurnComponents(event.getEntity(), entityData);

        if (entityData.hasComponents(event.getEntity(), ActiveTurnComponent.class)) {
            // trigger end turn and begin turn if you killed yourself
            NextTurnComponent nextTurn = entityData.getComponent(event.getEntity(), NextTurnComponent.class);
            List<Event> events = Lists.newArrayList(new EndTurnEvent(event.getEntity()), new BeginTurnEvent(nextTurn.getNextPlayer()));
            entityData.remove(event.getEntity(), ActiveTurnComponent.class);
            eventbus.registerSubEvents(events);
        }

        entityData.remove(event.getEntity(), NextTurnComponent.class);
    }

    private void updateNextTurnComponents(int entity, EntityData entityData) {
        NextTurnComponent component = entityData.getComponent(entity, NextTurnComponent.class);
        int entityBefore = entityData.list(NextTurnComponent.class).stream()
                .filter(e -> entityData.getComponent(e, NextTurnComponent.class).getNextPlayer() == entity)
                .findFirst()
                .orElse(-1);

        int entityAfter = component.getNextPlayer();
        entityData.addComponent(entityBefore, new NextTurnComponent(entityAfter));

    }
}
