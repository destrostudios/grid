package com.destrostudios.grid.eventbus.action.spellcasted;

import com.destrostudios.grid.components.character.TeamComponent;
import com.destrostudios.grid.components.map.PositionComponent;
import com.destrostudios.grid.components.properties.AttackPointsComponent;
import com.destrostudios.grid.components.properties.HealthPointsComponent;
import com.destrostudios.grid.components.properties.MovementPointsComponent;
import com.destrostudios.grid.components.spells.base.DamageComponent;
import com.destrostudios.grid.components.spells.base.HealComponent;
import com.destrostudios.grid.components.spells.buffs.*;
import com.destrostudios.grid.components.spells.limitations.CooldownComponent;
import com.destrostudios.grid.components.spells.limitations.CostComponent;
import com.destrostudios.grid.components.spells.limitations.OnCooldownComponent;
import com.destrostudios.grid.components.spells.movements.*;
import com.destrostudios.grid.components.spells.perturn.*;
import com.destrostudios.grid.components.spells.summon.SummonCastComponent;
import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.eventbus.Event;
import com.destrostudios.grid.eventbus.EventHandler;
import com.destrostudios.grid.eventbus.Eventbus;
import com.destrostudios.grid.eventbus.action.damagetaken.DamageTakenEvent;
import com.destrostudios.grid.eventbus.action.displace.PushEvent;
import com.destrostudios.grid.eventbus.action.healreceived.HealReceivedEvent;
import com.destrostudios.grid.eventbus.action.move.MoveEvent;
import com.destrostudios.grid.eventbus.action.move.MoveType;
import com.destrostudios.grid.eventbus.action.spawn.SummonCastEvent;
import com.destrostudios.grid.eventbus.action.swap.SwapEvent;
import com.destrostudios.grid.eventbus.add.playerbuff.PlayerBuffAddedEvent;
import com.destrostudios.grid.eventbus.add.poison.StatsPerTurnEvent;
import com.destrostudios.grid.eventbus.add.spellbuff.SpellBuffAddedEvent;
import com.destrostudios.grid.eventbus.add.summon.SummonBuffEvent;
import com.destrostudios.grid.eventbus.update.ap.AttackPointsChangedEvent;
import com.destrostudios.grid.eventbus.update.hp.HealthPointsChangedEvent;
import com.destrostudios.grid.eventbus.update.mp.MovementPointsChangedEvent;
import com.destrostudios.grid.random.RandomProxy;
import com.destrostudios.grid.util.SpellUtils;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@AllArgsConstructor
public class SpellCastedEventHandler implements EventHandler<SpellCastedEvent> {
  private final Eventbus eventbusInstance;
  private final RandomProxy randomProxy;

  @Override
  public void onEvent(SpellCastedEvent event, Supplier<EntityData> entityDataSupplier) {
    EntityData entityData = entityDataSupplier.get();
    int spell = event.getSpell();
    int playerEntity = event.getPlayerEntity();
    TeamComponent team = entityData.getComponent(playerEntity, TeamComponent.class);
    List<Event> followUpEvents = new ArrayList<>();
    List<Integer> affectedEntities =
        SpellUtils.getAffectedTargetableEntities(
            spell,
            event.getPlayerEntity(),
            entityData.getComponent(event.getPlayerEntity(), PositionComponent.class),
            new PositionComponent(event.getX(), event.getY()),
            entityData);

    addCooldownComponent(entityData, spell);
    addCostEvents(event, entityData, followUpEvents);
    addHealEvents(entityData, spell, playerEntity, team, followUpEvents, affectedEntities);
    addDamageEvents(entityData, spell, playerEntity, team, followUpEvents, affectedEntities);
    addPushEvents(event, entityData, spell, playerEntity, followUpEvents, affectedEntities);
    addPullEvents(event, entityData, spell, playerEntity, followUpEvents, affectedEntities);
    addDashEvents(event, entityData, spell, playerEntity, followUpEvents);
    addTeleportEvents(event, entityData, spell, playerEntity, followUpEvents);
    addSwapEvents(event, entityData, spell, playerEntity, followUpEvents);
    addBuffEvents(entityData, spell, playerEntity, followUpEvents);
    addStatsPerTurnEvents(entityData, spell, playerEntity, followUpEvents, affectedEntities);
    addCastsPerTurnComponent(entityData, spell);
    addSummonCastEvents(event, entityData, spell, followUpEvents);

    eventbusInstance.registerSubEvents(followUpEvents);
  }

  private void addCooldownComponent(EntityData entityData, int spell) {
    if (entityData.hasComponents(spell, CooldownComponent.class)) {
      entityData.addComponent(
          spell,
          new OnCooldownComponent(
              entityData.getComponent(spell, CooldownComponent.class).getCooldown()));
    }
  }

  private void addSummonCastEvents(
      SpellCastedEvent event, EntityData entityData, int spell, List<Event> followUpEvents) {
    SummonCastComponent summonComp = entityData.getComponent(spell, SummonCastComponent.class);
    if (summonComp != null) {
      List<Integer> affectedWalkableEntities =
          SpellUtils.getAffectedWalkableEntities(
              spell,
              entityData.getComponent(event.getPlayerEntity(), PositionComponent.class),
              new PositionComponent(event.getX(), event.getY()),
              entityData);

      for (Integer affectedWalkableEntity : affectedWalkableEntities) {
        PositionComponent component =
            entityData.getComponent(affectedWalkableEntity, PositionComponent.class);
        SummonCastComponent summonCastComponent =
            entityData.getComponent(spell, SummonCastComponent.class);
        followUpEvents.add(
            new SummonCastEvent(
                component, event.getPlayerEntity(), summonCastComponent.getSummonFile()));
      }
    }
  }

  private void addCastsPerTurnComponent(EntityData entityData, int spell) {
    if (entityData.hasComponents(spell, CastsPerTurnComponent.class)) {
      CastsPerTurnComponent castsPerTurnComponent =
          entityData.getComponent(spell, CastsPerTurnComponent.class);
      entityData.addComponent(
          spell,
          new CastsPerTurnComponent(
              castsPerTurnComponent.getMaxCastsPerTurn(),
              castsPerTurnComponent.getCastsThisTurn() + 1));
    }
  }

  private void addStatsPerTurnEvents(
      EntityData entityData,
      int spell,
      int playerEntity,
      List<Event> followUpEvents,
      List<Integer> affectedEntities) {
    if (entityData.hasComponents(spell, AttackPointsPerTurnComponent.class)
        || entityData.hasComponents(spell, MovementPointsPerTurnComponent.class)
        || entityData.hasComponents(spell, DamagePerTurnComponent.class)
        || entityData.hasComponents(spell, HealPerTurnComponent.class)) {
      for (Integer affectedEntity : affectedEntities) {
        followUpEvents.add(new StatsPerTurnEvent(playerEntity, affectedEntity, spell));
      }
    }
  }

  private void addBuffEvents(
      EntityData entityData, int spell, int playerEntity, List<Event> followUpEvents) {
    List<Event> buffEvents = getBuffEvents(entityData, spell, playerEntity);
    if (!buffEvents.isEmpty()) {
      followUpEvents.addAll(buffEvents);
    }
  }

  private void addSwapEvents(
      SpellCastedEvent event,
      EntityData entityData,
      int spell,
      int playerEntity,
      List<Event> followUpEvents) {
    if (entityData.hasComponents(spell, SwapComponent.class)) {
      if (!entityData.hasComponents(spell, AntiCCBuffComponent.class)) {
        int targetEntity = SpellUtils.calculateTargetEntity(event.getX(), event.getY(), entityData);
        followUpEvents.add(new SwapEvent(playerEntity, targetEntity));
      }
    }
  }

  private void addTeleportEvents(
      SpellCastedEvent event,
      EntityData entityData,
      int spell,
      int playerEntity,
      List<Event> followUpEvents) {
    if (entityData.hasComponents(spell, TeleportComponent.class)) {
      followUpEvents.add(
          new MoveEvent(
              playerEntity, new PositionComponent(event.getX(), event.getY()), MoveType.TELEPORT));
    }
  }

  private void addDashEvents(
      SpellCastedEvent event,
      EntityData entityData,
      int spell,
      int playerEntity,
      List<Event> followUpEvents) {
    DashComponent dash = entityData.getComponent(spell, DashComponent.class);
    if (dash != null) {
      PositionComponent source = entityData.getComponent(playerEntity, PositionComponent.class);
      PositionComponent target = new PositionComponent(event.getX(), event.getY());
      PositionComponent dashTarget =
          SpellUtils.getDisplacementGoal(
              entityData,
              playerEntity,
              source,
              SpellUtils.directionForDelta(source, target),
              dash.getDistance());
      followUpEvents.add(new MoveEvent(playerEntity, dashTarget, MoveType.DASH));
    }
  }

  private void addPullEvents(
      SpellCastedEvent event,
      EntityData entityData,
      int spell,
      int playerEntity,
      List<Event> followUpEvents,
      List<Integer> affectedEntities) {
    PullComponent pull = entityData.getComponent(spell, PullComponent.class);
    if (pull != null) {
      PositionComponent pullOrigin;
      if (pull.isUseTargetAsOrigin()) {
        pullOrigin = new PositionComponent(event.getX(), event.getY());
      } else {
        pullOrigin = entityData.getComponent(playerEntity, PositionComponent.class);
      }
      for (Integer affectedEntity : affectedEntities) {
        if (!entityData.hasComponents(affectedEntity, AntiCCBuffComponent.class)) {
          PositionComponent target =
              entityData.getComponent(affectedEntity, PositionComponent.class);
          PositionComponent pullTarget =
              SpellUtils.getDisplacementGoal(
                  entityData,
                  affectedEntity,
                  target,
                  SpellUtils.directionForDelta(target, pullOrigin),
                  pull.getDistance());
          followUpEvents.add(new MoveEvent(affectedEntity, pullTarget, MoveType.PULL));
        }
      }
    }
  }

  private void addPushEvents(
      SpellCastedEvent event,
      EntityData entityData,
      int spell,
      int playerEntity,
      List<Event> followUpEvents,
      List<Integer> affectedEntities) {
    PushComponent push = entityData.getComponent(spell, PushComponent.class);
    if (push != null) {
      PositionComponent pushOrigin;
      if (push.isUseTargetAsOrigin()) {
        pushOrigin = new PositionComponent(event.getX(), event.getY());
      } else {
        pushOrigin = entityData.getComponent(playerEntity, PositionComponent.class);
      }

      for (Integer affectedEntity : affectedEntities) {
        PositionComponent target = entityData.getComponent(affectedEntity, PositionComponent.class);
        followUpEvents.add(
            new PushEvent(
                affectedEntity,
                push.getDisplacement(),
                SpellUtils.directionForDelta(pushOrigin, target)));
      }
    }
  }

  private void addDamageEvents(
      EntityData entityData,
      int spell,
      int playerEntity,
      TeamComponent team,
      List<Event> followUpEvents,
      List<Integer> affectedEntities) {
    if (entityData.hasComponents(spell, DamageComponent.class)) {
      DamageComponent damage = entityData.getComponent(spell, DamageComponent.class);
      int damageAmount = randomProxy.nextInt(damage.getMinDmg(), damage.getMaxDmg());

      for (Integer affectedEntity : affectedEntities) {
        TeamComponent teamAffectedEntity =
            entityData.getComponent(affectedEntity, TeamComponent.class);
        if (damage.isTargetingAllies() || teamAffectedEntity.getTeam() != team.getTeam()) {
          followUpEvents.add(
              new DamageTakenEvent(
                  damageAmount
                      + SpellUtils.getBuffAmount(
                          spell, playerEntity, entityData, DamageBuffComponent.class),
                  playerEntity,
                  affectedEntity,
                  false));
        }
      }
    }
  }

  private void addHealEvents(
      EntityData entityData,
      int spell,
      int playerEntity,
      TeamComponent team,
      List<Event> followUpEvents,
      List<Integer> affectedEntities) {
    if (entityData.hasComponents(spell, HealComponent.class)) {
      HealComponent heal = entityData.getComponent(spell, HealComponent.class);
      int healAmount = randomProxy.nextInt(heal.getMinHeal(), heal.getMaxHeal());

      for (Integer affectedEntity : affectedEntities) {
        TeamComponent teamAffectedEntity =
            entityData.getComponent(affectedEntity, TeamComponent.class);
        if (heal.isTargetingEnemies() || teamAffectedEntity.getTeam() == team.getTeam()) {
          followUpEvents.add(
              new HealReceivedEvent(
                  healAmount
                      + SpellUtils.getBuffAmount(
                          spell, playerEntity, entityData, HealBuffComponent.class),
                  affectedEntity));
        }
      }
    }
  }

  private void addCostEvents(
      SpellCastedEvent event, EntityData entityData, List<Event> followUpEvents) {
    if (entityData.hasComponents(event.getSpell(), CostComponent.class)) {
      CostComponent costComponent = entityData.getComponent(event.getSpell(), CostComponent.class);
      AttackPointsComponent ap =
          entityData.getComponent(event.getPlayerEntity(), AttackPointsComponent.class);
      MovementPointsComponent mp =
          entityData.getComponent(event.getPlayerEntity(), MovementPointsComponent.class);
      HealthPointsComponent hp =
          entityData.getComponent(event.getPlayerEntity(), HealthPointsComponent.class);

      if (costComponent.getApCost() > 0) {
        followUpEvents.add(
            new AttackPointsChangedEvent(
                event.getPlayerEntity(), ap.getAttackPoints() - costComponent.getApCost()));
      }
      if (costComponent.getMpCost() > 0) {
        followUpEvents.add(
            new MovementPointsChangedEvent(
                event.getPlayerEntity(), mp.getMovementPoints() - costComponent.getMpCost()));
      }
      if (costComponent.getHpCost() > 0) {
        followUpEvents.add(
            new HealthPointsChangedEvent(
                event.getPlayerEntity(), hp.getHealth() - costComponent.getHpCost()));
      }
    }
  }

  private List<Event> getBuffEvents(EntityData entityData, int spell, int playerEntity) {
    List<Event> followUpEvents = new ArrayList<>();

    boolean hasBuff =
        entityData.getComponents(spell).stream().anyMatch(c -> c instanceof BuffComponent);

    boolean hasSpellBuff =
        entityData.getComponents(spell).stream()
            .filter(c -> c instanceof BuffComponent)
            .map(c -> (BuffComponent) c)
            .anyMatch(c -> c.getBuffType() == BuffType.SPELL);

    boolean hasPlayerBuff =
        entityData.getComponents(spell).stream()
            .filter(c -> c instanceof BuffComponent)
            .map(c -> (BuffComponent) c)
            .anyMatch(c -> c.getBuffType() == BuffType.PLAYER);
    boolean hasSummonBuff =
        entityData.getComponents(spell).stream()
            .filter(c -> c instanceof BuffComponent)
            .map(c -> (BuffComponent) c)
            .anyMatch(c -> c.getBuffType() == BuffType.SUMMON);

    if (hasBuff) {
      if (hasSpellBuff) {
        followUpEvents.add(new SpellBuffAddedEvent(spell));
      }
      if (hasPlayerBuff) {
        followUpEvents.add(new PlayerBuffAddedEvent(playerEntity, spell));
      }
      if (hasSummonBuff) {
        followUpEvents.add(new SummonBuffEvent(playerEntity, spell));
      }
    }
    return followUpEvents;
  }
}
