package com.destrostudios.grid.eventbus.action.spellcasted;

import com.destrostudios.grid.components.character.PlayerComponent;
import com.destrostudios.grid.components.map.ObstacleComponent;
import com.destrostudios.grid.components.map.PositionComponent;
import com.destrostudios.grid.components.map.WalkableComponent;
import com.destrostudios.grid.components.properties.AttackPointsComponent;
import com.destrostudios.grid.components.properties.HealthPointsComponent;
import com.destrostudios.grid.components.properties.MovementPointsComponent;
import com.destrostudios.grid.components.properties.SummonComponent;
import com.destrostudios.grid.components.spells.limitations.CostComponent;
import com.destrostudios.grid.components.spells.limitations.OnCooldownComponent;
import com.destrostudios.grid.components.spells.limitations.RequiresTargetComponent;
import com.destrostudios.grid.components.spells.movements.TeleportComponent;
import com.destrostudios.grid.components.spells.summon.SummonCastComponent;
import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.eventbus.EventValidator;
import com.destrostudios.grid.serialization.ComponentsContainerSerializer;
import com.destrostudios.grid.serialization.container.SummonContainer;
import com.destrostudios.grid.util.SpellUtils;
import lombok.SneakyThrows;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.destrostudios.grid.util.RangeUtils.isFieldTargetable;
import static com.destrostudios.grid.util.SpellUtils.isPositionIsFree;

public class SpellCastedValidator implements EventValidator<SpellCastedEvent> {
  @Override
  public boolean validate(SpellCastedEvent event, Supplier<EntityData> entityDataSupplier) {
    EntityData entityData = entityDataSupplier.get();

    // check Range
    int target = SpellUtils.calculateTargetEntity(event.getX(), event.getY(), entityData);
    PositionComponent position = entityData.getComponent(target, PositionComponent.class);
    MovementPointsComponent movementPointsPlayer =
        entityData.getComponent(event.getPlayerEntity(), MovementPointsComponent.class);
    HealthPointsComponent healthPointsComponent =
        entityData.getComponent(event.getPlayerEntity(), HealthPointsComponent.class);
    CostComponent costComponent = entityData.getComponent(event.getSpell(), CostComponent.class);

    boolean fieldIsReachable =
        isFieldTargetable(event.getSpell(), event.getPlayerEntity(), entityData, position);
    boolean isOnCooldown = entityData.hasComponents(event.getSpell(), OnCooldownComponent.class);
    boolean positionIsFree = isPositionIsFree(entityData, position, event.getPlayerEntity());
    boolean teleportCanBeDone =
        !entityData.hasComponents(event.getSpell(), TeleportComponent.class)
            || positionIsFree
                && entityData.hasComponents(event.getSpell(), TeleportComponent.class);
    boolean costsCanBePayed =
        isCostsCanBePayed(
            event, entityData, movementPointsPlayer, healthPointsComponent, costComponent);
    boolean requiresTarget =
        !entityData.hasComponents(event.getSpell(), RequiresTargetComponent.class)
            || entityData.hasComponents(target, PlayerComponent.class);
    boolean summonCanBeCasted = summonCanBeCasted(event, entityDataSupplier);
    return requiresTarget
        && fieldIsReachable
        && !isOnCooldown
        && teleportCanBeDone
        && summonCanBeCasted
        && costsCanBePayed
        && SpellUtils.maxCastsNotReached(event.getSpell(), entityData);
  }

  private boolean isCostsCanBePayed(
      SpellCastedEvent event,
      EntityData entityData,
      MovementPointsComponent movementPointsPlayer,
      HealthPointsComponent healthPointsComponent,
      CostComponent costComponent) {
    AttackPointsComponent attackPointsPlayer =
        entityData.getComponent(event.getPlayerEntity(), AttackPointsComponent.class);
    if (entityData.hasComponents(event.getPlayerEntity(), SummonComponent.class)
        && attackPointsPlayer == null
        && movementPointsPlayer == null
        && healthPointsComponent == null) {
      return true;
    }
    return attackPointsPlayer.getAttackPoints() >= costComponent.getApCost()
        && movementPointsPlayer.getMovementPoints() >= costComponent.getMpCost()
        && healthPointsComponent.getHealth() >= costComponent.getHpCost();
  }

  @SneakyThrows
  private boolean summonCanBeCasted(
      SpellCastedEvent event, Supplier<EntityData> entityDataSupplier) {
    EntityData entityData = entityDataSupplier.get();
    SummonCastComponent summonCastComponent =
        entityData.getComponent(event.getSpell(), SummonCastComponent.class);

    if (summonCastComponent != null) {
      String summonFile = summonCastComponent.getSummonFile();
      SummonContainer summonContainer =
          ComponentsContainerSerializer.readSeriazableFromRessources(
              summonFile, SummonContainer.class);

      boolean summonHasObstacleComp =
          summonContainer.getProperties().stream()
              .anyMatch(c -> c.getClass().equals(ObstacleComponent.class));
      if (summonHasObstacleComp) {
        Stream<PositionComponent> affectedWalkablePositions =
                SpellUtils.getAffectedWalkableEntities(
                                event.getSpell(),
                                entityData.getComponent(event.getPlayerEntity(), PositionComponent.class),
                                new PositionComponent(event.getX(), event.getY()),
                                entityData)
                        .stream()
                        .map(e -> entityData.getComponent(e, PositionComponent.class));

        return affectedWalkablePositions
                .noneMatch(walkablePosition -> entityData.findEntitiesByComponentValue(walkablePosition).stream()
                        .anyMatch(e -> !entityData.hasComponents(e, WalkableComponent.class)));
      }
    }
    return true;
  }
}
