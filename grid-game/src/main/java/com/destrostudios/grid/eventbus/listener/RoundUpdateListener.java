package com.destrostudios.grid.eventbus.listener;

import com.destrostudios.grid.GridGame;
import com.destrostudios.grid.components.AttackPointsComponent;
import com.destrostudios.grid.components.MovementPointsComponent;
import com.destrostudios.grid.components.RoundComponent;
import com.destrostudios.grid.components.PlayerComponent;
import com.destrostudios.grid.entities.EntityMap;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.ComponentUpdateEvent;
import com.destrostudios.grid.eventbus.Listener;
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
        boolean hasRoundComponent = entityWorld.hasComponents(componentUpdateEvent.getEntity(), RoundComponent.class);
        int activePlayerEntity = entityWorld.list(RoundComponent.class).stream()
                .findFirst()
                .orElse(-1);

        if (playerEntities.size() == 1 || activePlayerEntity == -1) {
            logger.warning("Just one player, cant switch turn!");

        } else if (hasRoundComponent) {
            // TODO: 09.02.2021 this should depend on the "round order"
            Optional<Integer> newActivePlayer = playerEntities.stream()
                    .filter(i -> i != activePlayerEntity)
                    .findFirst();
            int nextActivePlayerEntity = newActivePlayer.orElse(activePlayerEntity);
            resetBattleComponents(activePlayerEntity, entityWorld);
            entityWorld.addComponent(nextActivePlayerEntity, new RoundComponent());
            logger.info(String.format("Switched activen round from player %s to player %s", activePlayerEntity, nextActivePlayerEntity));
        }
    }

    private void resetBattleComponents(int playerEntity, EntityWorld entityWorld) {
        entityWorld.remove(playerEntity, AttackPointsComponent.class);
        entityWorld.remove(playerEntity, MovementPointsComponent.class);
        entityWorld.remove(playerEntity, RoundComponent.class);
        entityWorld.addComponent(playerEntity, new MovementPointsComponent(GridGame.MAX_MP));
        entityWorld.addComponent(playerEntity, new AttackPointsComponent(GridGame.MAX_MP));

    }

    @Override
    public Class<RoundComponent> supports() {
        return RoundComponent.class;
    }
}
