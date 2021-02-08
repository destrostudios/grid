package com.destrostudios.grid.update.listener;

import com.destrostudios.grid.components.RoundComponent;
import com.destrostudios.grid.components.PlayerComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.update.eventbus.ComponentUpdateEvent;
import com.destrostudios.grid.update.eventbus.Listener;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@AllArgsConstructor
public class RoundUpdateListener implements Listener<RoundComponent> {

    private final static Logger logger = Logger.getLogger(PositionUpdateListener.class.getSimpleName());

    @Override
    public void handle(ComponentUpdateEvent<RoundComponent> componentUpdateEvent, EntityWorld entityWorld) {
        List<Integer> playerEntities = entityWorld.list(PlayerComponent.class);
        int activePlayerEntity = entityWorld.list(RoundComponent.class).stream()
                .findFirst()
                .orElse(-1);
        
        if (playerEntities.size() == 1 || activePlayerEntity == -1) {
            logger.warning("Just one player, cant switch turn!");

        } else if (activePlayerEntity == componentUpdateEvent.getEntity()) {
            Optional<Integer> newActivePlayer = playerEntities.stream()
                    .filter(i -> i != activePlayerEntity)
                    .findFirst();
            int nextActivePlayerEntity = newActivePlayer.orElse(activePlayerEntity);
            entityWorld.remove(activePlayerEntity, RoundComponent.class);
            entityWorld.addComponent(nextActivePlayerEntity, new RoundComponent());
            logger.info(String.format("Switched activen round from player %s to player %s", activePlayerEntity, nextActivePlayerEntity));
        }
    }

    @Override
    public Class<RoundComponent> supports() {
        return RoundComponent.class;
    }
}
