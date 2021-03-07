package com.destrostudios.grid.eventbus.action.displace;

import com.destrostudios.grid.components.map.PositionComponent;
import com.destrostudios.grid.components.properties.HealthPointsComponent;
import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.eventbus.Event;
import com.destrostudios.grid.eventbus.EventHandler;
import com.destrostudios.grid.eventbus.Eventbus;
import com.destrostudios.grid.eventbus.update.hp.HealthPointsChangedEvent;
import com.destrostudios.grid.eventbus.update.position.PositionUpdateEvent;
import com.destrostudios.grid.util.RangeUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DisplacementHandler implements EventHandler<DisplacementEvent> {
    private final Eventbus eventbus;

    @Override
    public void onEvent(DisplacementEvent event, Supplier<EntityData> entityDataSupplier) {
        EntityData entityData = entityDataSupplier.get();
        int entityToDisplace = event.getEntityToDisplace();
        List<Event> followUpEvents = new ArrayList<>();

        PositionComponent posEntityToDisplace = entityData.getComponent(entityToDisplace, PositionComponent.class);
        PositionComponent posSource = new PositionComponent(event.getXDisplacementSource(), event.getYDisplacementSource());

        PositionComponent resultingPos = RangeUtils.getDisplacementGoal(entityData, posEntityToDisplace, posSource, event.getEntityToDisplace(), event.getDisplacementAmount());
        int actualDisplacement = Math.abs(resultingPos.getX() - posEntityToDisplace.getX()) + Math.abs(resultingPos.getY() - posEntityToDisplace.getY());
        int displacementDmg = getDisplacementDmg(actualDisplacement, Math.abs(event.getDisplacementAmount()));
        followUpEvents.add(new PositionUpdateEvent(event.getEntityToDisplace(), resultingPos));

        if (displacementDmg != 0) {
            HealthPointsComponent hpComponent = entityData.getComponent(entityToDisplace, HealthPointsComponent.class);
            followUpEvents.add(new HealthPointsChangedEvent(entityToDisplace, hpComponent.getHealth() - displacementDmg));
        }
        eventbus.registerSubEvents(followUpEvents);
    }

    private int getDisplacementDmg(int actualDisplacement, int plannedDisplacement) {
        return (plannedDisplacement - actualDisplacement) * 10;
    }
}
