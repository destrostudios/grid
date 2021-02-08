package com.destrostudios.grid.update.listener;

import com.destrostudios.grid.components.MovingComponent;
import com.destrostudios.grid.components.PositionComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.update.eventbus.ComponentUpdateEvent;
import com.destrostudios.grid.update.eventbus.Listener;
import lombok.AllArgsConstructor;

import java.util.logging.Logger;

@AllArgsConstructor
public class PositionUpdateListener implements Listener<PositionComponent> {
    private final static Logger logger = Logger.getLogger(PositionUpdateListener.class.getSimpleName());


    @Override
    public void handle(ComponentUpdateEvent<PositionComponent> componentUpdateEvent, EntityWorld entityWorld) {
        final PositionComponent positionComponent = componentUpdateEvent.getComponent();
        boolean hasMovingAndPosComp = entityWorld.hasComponents(componentUpdateEvent.getEntity(), componentUpdateEvent.getComponent().getClass(), MovingComponent.class);

        if (hasMovingAndPosComp) {
            entityWorld.remove(componentUpdateEvent.getEntity(), PositionComponent.class);
            entityWorld.addComponent(componentUpdateEvent.getEntity(), positionComponent);
            logger.info(String.format("Entity %s moved to (%s|%s)", componentUpdateEvent.getEntity(), positionComponent.getX(), positionComponent.getY()));
        }
    }

    @Override
    public Class<PositionComponent> supports() {
        return PositionComponent.class;
    }
}
