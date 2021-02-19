package com.destrostudios.grid.eventbus.handler.properties;

import com.destrostudios.grid.components.properties.MovementPointsComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.events.PropertiePointsChangedEvent;
import com.destrostudios.grid.eventbus.handler.EventHandler;

import java.util.function.Supplier;
import java.util.logging.Logger;

public class MovementPointsChangedHandler implements EventHandler<PropertiePointsChangedEvent.MovementPointsChangedEvent> {
    private final static Logger logger = Logger.getLogger(MovementPointsChangedHandler.class.getSimpleName());

    @Override
    public void onEvent(PropertiePointsChangedEvent.MovementPointsChangedEvent event, Supplier<EntityWorld> entityWorldSupplier) {
        entityWorldSupplier.get().addComponent( event.getEntity(), new MovementPointsComponent(event.getNewPoints()));
    }
}
