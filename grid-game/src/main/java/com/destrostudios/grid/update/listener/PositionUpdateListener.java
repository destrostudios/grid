package com.destrostudios.grid.update.listener;

import com.destrostudios.grid.components.MovingComponent;
import com.destrostudios.grid.components.PositionComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.update.ComponentUpdateEvent;
import lombok.AllArgsConstructor;

import java.util.logging.Logger;

@AllArgsConstructor
public class PositionUpdateListener implements ComponentUpdateListener<ComponentUpdateEvent<PositionComponent>> {
    private final static Logger logger = Logger.getLogger(PositionUpdateListener.class.getSimpleName());

    private final EntityWorld world;

    @Override
    public void handleEvent(ComponentUpdateEvent<PositionComponent> event) {
        final PositionComponent positionComponent = event.getComponent();
        boolean hasMovinAndPosComp = world.hasComponents(event.getEntity(), event.getComponent().getClass(), MovingComponent.class);

        if (hasMovinAndPosComp) {
            world.remove(event.getEntity(), PositionComponent.class);
            world.addComponent(event.getEntity(), positionComponent);
            logger.info(String.format("Entity %s moved to (%s|%s)", event.getEntity(), positionComponent.getX(), positionComponent.getY()));
        }

    }
}
