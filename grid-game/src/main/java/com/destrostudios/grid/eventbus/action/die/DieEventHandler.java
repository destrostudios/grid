package com.destrostudios.grid.eventbus.action.die;

import com.destrostudios.grid.components.character.ActiveTurnComponent;
import com.destrostudios.grid.components.character.NextTurnComponent;
import com.destrostudios.grid.components.map.ObstacleComponent;
import com.destrostudios.grid.components.map.VisionComponent;
import com.destrostudios.grid.components.properties.ActiveSummonsComponent;
import com.destrostudios.grid.components.properties.IsAliveComponent;
import com.destrostudios.grid.components.properties.SummonComponent;
import com.destrostudios.grid.components.spells.ontouch.SpellOnTouchComponent;
import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.eventbus.Event;
import com.destrostudios.grid.eventbus.EventHandler;
import com.destrostudios.grid.eventbus.Eventbus;
import com.destrostudios.grid.eventbus.action.beginturn.BeginTurnEvent;
import com.destrostudios.grid.eventbus.action.endturn.EndTurnEvent;
import com.destrostudios.grid.util.GameOverInfo;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

@AllArgsConstructor
public class DieEventHandler implements EventHandler<DieEvent> {
  private final GameOverInfo gameOverInfo;
  private final Eventbus eventbus;

  @Override
  public void onEvent(DieEvent event, Supplier<EntityData> entityDataSupplier) {
    EntityData entityData = entityDataSupplier.get();
    int dieEntity = event.getEntity();

    if (entityData.hasComponents(dieEntity, SummonComponent.class)) {
      removeComponentsForDyingSummon(entityData, dieEntity);
    } else {
      removeComponentsForDyingPlayer(entityData, dieEntity);
    }
  }

  private void removeComponentsForDyingPlayer(EntityData entityData, int dieEntity) {
    entityData.remove(dieEntity, ObstacleComponent.class);
    entityData.remove(dieEntity, VisionComponent.class);
    entityData.remove(dieEntity, IsAliveComponent.class);

    gameOverInfo.checkGameOverStatus();
    updateNextTurnComponents(dieEntity, entityData);

    if (entityData.hasComponents(dieEntity, ActiveTurnComponent.class)) {
      // trigger end turn and begin turn if you killed yourself
      NextTurnComponent nextTurn = entityData.getComponent(dieEntity, NextTurnComponent.class);
      List<Event> events =
          Lists.newArrayList(
              new EndTurnEvent(dieEntity), new BeginTurnEvent(nextTurn.getNextPlayer()));
      entityData.remove(dieEntity, ActiveTurnComponent.class);
      eventbus.registerSubEvents(events);
    }

    entityData.remove(dieEntity, NextTurnComponent.class);
  }

  private void removeComponentsForDyingSummon(EntityData entityData, int dieEntity) {
    Optional<Integer> playerWithSummonOpt =
        entityData.list(ActiveSummonsComponent.class).stream()
            .filter(
                e ->
                    entityData
                        .getComponent(e, ActiveSummonsComponent.class)
                        .getActiveSummons()
                        .contains(dieEntity))
            .findFirst();
    SpellOnTouchComponent spellOnTouchComponent =
        entityData.getComponent(dieEntity, SpellOnTouchComponent.class);
    if (spellOnTouchComponent != null) {
      entityData.removeEntity(spellOnTouchComponent.getSpell());
    }
    if (playerWithSummonOpt.isPresent()) {
      int playerEntity = playerWithSummonOpt.get();
      ActiveSummonsComponent activeSummonsComponent =
          entityData.getComponent(playerEntity, ActiveSummonsComponent.class);
      Set<Integer> newActiveSummons =
          new LinkedHashSet<>(activeSummonsComponent.getActiveSummons());
      newActiveSummons.remove(dieEntity);
      entityData.addComponent(playerEntity, new ActiveSummonsComponent(newActiveSummons));
      entityData.removeEntity(dieEntity);
    }
  }

  private void updateNextTurnComponents(int entity, EntityData entityData) {
    NextTurnComponent component = entityData.getComponent(entity, NextTurnComponent.class);
    int entityBefore =
        entityData.list(NextTurnComponent.class).stream()
            .filter(
                e -> entityData.getComponent(e, NextTurnComponent.class).getNextPlayer() == entity)
            .findFirst()
            .orElse(-1);

    int entityAfter = component.getNextPlayer();
    entityData.addComponent(entityBefore, new NextTurnComponent(entityAfter));
  }
}
