package com.destrostudios.grid.eventbus.action.endturn;

import com.destrostudios.grid.components.properties.MaxAttackPointsComponent;
import com.destrostudios.grid.components.properties.MaxMovementPointsComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.Event;
import com.destrostudios.grid.eventbus.EventHandler;
import com.destrostudios.grid.eventbus.Eventbus;
import com.destrostudios.grid.eventbus.update.ap.AttackPointsChangedEvent;
import com.destrostudios.grid.eventbus.update.mp.MovementPointsChangedEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EndTurnHandler implements EventHandler<EndTurnEvent> {
    private final Eventbus eventbus;

    @Override
    public void onEvent(EndTurnEvent event, Supplier<EntityWorld> entityWorldSupplier) {
        EntityWorld entityWorld = entityWorldSupplier.get();
        int currentEntity = event.getEndTurnEntity();

        List<Event> followUpEvents = new ArrayList<>();
        MaxAttackPointsComponent maxAp = entityWorld.getComponent(currentEntity, MaxAttackPointsComponent.class);
        MaxMovementPointsComponent maxMp = entityWorld.getComponent(currentEntity, MaxMovementPointsComponent.class);

        // reset ap and mp end of the round
        followUpEvents.add(new AttackPointsChangedEvent(currentEntity, maxAp.getMaxAttackPoints()));
        followUpEvents.add(new MovementPointsChangedEvent(currentEntity, maxMp.getMaxMovementPoints()));

        eventbus.registerSubEvents(followUpEvents);
    }
}
