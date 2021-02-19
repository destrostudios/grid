package com.destrostudios.grid.eventbus.handler;

import com.destrostudios.grid.components.character.PlayerComponent;
import com.destrostudios.grid.components.character.RoundComponent;
import com.destrostudios.grid.components.properties.AttackPointsComponent;
import com.destrostudios.grid.components.properties.MaxAttackPointsComponent;
import com.destrostudios.grid.components.properties.MaxMovementPointsComponent;
import com.destrostudios.grid.components.properties.MovementPointsComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.Eventbus;
import com.destrostudios.grid.eventbus.events.RoundSkippedEvent;
import com.destrostudios.grid.eventbus.events.SimpleUpdateEvent;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Logger;

@AllArgsConstructor
public class RoundSkippedHandler implements EventHandler<RoundSkippedEvent> {

    private final static Logger logger = Logger.getLogger(RoundSkippedHandler.class.getSimpleName());

    private final Eventbus instance;

    private void resetBattleComponents(int playerEntity, EntityWorld entityWorld) {
        Optional<MaxAttackPointsComponent> maxAp = entityWorld.getComponent(playerEntity, MaxAttackPointsComponent.class);
        Optional<MaxMovementPointsComponent> maxMp = entityWorld.getComponent(playerEntity, MaxMovementPointsComponent.class);
        entityWorld.remove(playerEntity, AttackPointsComponent.class);
        entityWorld.remove(playerEntity, MovementPointsComponent.class);
        entityWorld.remove(playerEntity, RoundComponent.class);
        entityWorld.addComponent(playerEntity, new MovementPointsComponent(maxMp.get().getMaxMovenemtPoints()));
        entityWorld.addComponent(playerEntity, new AttackPointsComponent(maxAp.get().getMaxAttackPoints()));
    }


    @Override
    public void onEvent(RoundSkippedEvent event, Supplier<EntityWorld> entityWorldSupplier) {
        EntityWorld entityWorld = entityWorldSupplier.get();
        int currentEntity = event.getEntity();
        resetBattleComponents(currentEntity, entityWorld);
        entityWorld.remove(event.getEntity(), RoundComponent.class);
        Optional<Integer> newActivePlayer = entityWorld.list(PlayerComponent.class).stream()
                .filter(i -> i != currentEntity)
                .findFirst();
        entityWorld.addComponent(newActivePlayer.get(), new RoundComponent());

        instance.registerSubEvents(List.of(new SimpleUpdateEvent.BuffsUpdateEvent(newActivePlayer.get()), new SimpleUpdateEvent.UpdateCooldownsUpdateEvent(newActivePlayer.get()))) ;
    }
}
