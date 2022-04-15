package com.destrostudios.grid.util;

import com.destrostudios.grid.components.spells.base.DamageComponent;
import com.destrostudios.grid.components.spells.base.HealComponent;
import com.destrostudios.grid.components.spells.base.TooltipComponent;
import com.destrostudios.grid.components.spells.buffs.*;
import com.destrostudios.grid.components.spells.limitations.CooldownComponent;
import com.destrostudios.grid.components.spells.limitations.CostComponent;
import com.destrostudios.grid.components.spells.limitations.RequiresTargetComponent;
import com.destrostudios.grid.components.spells.movements.*;
import com.destrostudios.grid.components.spells.perturn.*;
import com.destrostudios.grid.components.spells.range.LineOfSightComponent;
import com.destrostudios.grid.components.spells.range.RangeComponent;
import com.destrostudios.grid.components.spells.summon.SummonCastComponent;
import com.destrostudios.grid.entities.EntityData;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TooltipsGenerator {

  public static TooltipComponent generateTooltip(EntityData entityData, int spell) {
    String tooltip =
        getPrefix(entityData, spell)
            + getEffectsString(entityData, spell)
            + "\n"
            + getSuffix(entityData, spell);
    return new TooltipComponent(tooltip);
  }

  private static String getPrefix(EntityData entityData, int spell) {
    return getCostsAndRange(entityData, spell);
  }

  private static String getEffectsString(EntityData entityData, int spell) {
    List<String> result = new ArrayList<>();
    result.add(getDamageAndHealString(entityData, spell));
    result.add(getBuffString(entityData, spell));
    result.add(getStatsPerTurnString(entityData, spell));
    result.add(getUtilityString(entityData, spell));

    if (result.isEmpty()) {
      return "";
    }
    return result.stream().filter(t -> !t.equals("")).collect(Collectors.joining());
  }

  private static String getSuffix(EntityData entityData, int spell) {
    return getRestrictionsString(entityData, spell);
  }

  private static String getUtilityString(EntityData entityData, int spell) {
    List<String> utilities = new ArrayList<>();

    if (entityData.hasComponents(spell, DashComponent.class)) {
      DashComponent component = entityData.getComponent(spell, DashComponent.class);
      utilities.add("Dashes " + component.getDistance() + " fields");
    }
    if (entityData.hasComponents(spell, PullComponent.class)) {
      PullComponent component = entityData.getComponent(spell, PullComponent.class);
      String string =
          component.isUseTargetAsOrigin()
              ? "Pulls " + component.getDistance() + " with target as origin"
              : "Pulls " + component.getDistance() + " with caster as origin";
      utilities.add(string);
    }
    if (entityData.hasComponents(spell, PushComponent.class)) {
      PushComponent component = entityData.getComponent(spell, PushComponent.class);
      String string =
          component.isUseTargetAsOrigin()
              ? "Pushes " + component.getDisplacement() + " with target as origin"
              : "Pushes " + component.getDisplacement() + " with caster as origin";
      utilities.add(string);
    }
    if (entityData.hasComponents(spell, SwapComponent.class)) {
      utilities.add("Swaps position with the target enemy");
    }
    if (entityData.hasComponents(spell, TeleportComponent.class)) {
      utilities.add("Teleports to target free position");
    }
    if (entityData.hasComponents(spell, SummonCastComponent.class)) {
      utilities.add("Spawns a summon to target free position");
    }

    return !utilities.isEmpty() ? String.join(", ", utilities) : "";
  }

  private static String getRestrictionsString(EntityData entityData, int spell) {
    List<String> restrictions = new ArrayList<>();

    RangeComponent range = entityData.getComponent(spell, RangeComponent.class);
    String rangeString =
        range.getMinRange() == range.getMaxRange()
            ? "range: " + range.getMinRange()
            : "range: " + range.getMinRange() + " - " + range.getMaxRange();
    restrictions.add(rangeString);

    String lineOfSight =
        entityData.hasComponents(spell, LineOfSightComponent.class)
            ? "needs line of sight"
            : "no line of sight";
    restrictions.add(lineOfSight);

    if (entityData.hasComponents(spell, RequiresTargetComponent.class)) {
      restrictions.add("requires target");
    }
    if (entityData.hasComponents(spell, CooldownComponent.class)) {
      CooldownComponent component = entityData.getComponent(spell, CooldownComponent.class);
      restrictions.add("cooldown " + component.getCooldown());
    }
    if (entityData.hasComponents(spell, CastsPerTurnComponent.class)) {
      CastsPerTurnComponent component = entityData.getComponent(spell, CastsPerTurnComponent.class);
      restrictions.add("casts per turn" + component.getMaxCastsPerTurn());
    }

    return !restrictions.isEmpty() ? "(" + String.join(" | ", restrictions) + ")" : "";
  }

  private static String getStatsPerTurnString(EntityData entityData, int spell) {
    List<String> statsPerTurn = new ArrayList<>();
    if (entityData.hasComponents(spell, DamagePerTurnComponent.class)) {
      DamagePerTurnComponent dmgPerTurn =
          entityData.getComponent(spell, DamagePerTurnComponent.class);
      statsPerTurn.add(
          String.format(
              "%s-%s dmg per turn for %s rounds",
              dmgPerTurn.getMinValue(), dmgPerTurn.getMaxValue(), dmgPerTurn.getDuration()));
    }
    if (entityData.hasComponents(spell, HealPerTurnComponent.class)) {
      HealPerTurnComponent healPerTurn = entityData.getComponent(spell, HealPerTurnComponent.class);
      statsPerTurn.add(
          String.format(
              "%s-%s heal per turn for %s rounds",
              healPerTurn.getMinValue(), healPerTurn.getMaxValue(), healPerTurn.getDuration()));
    }
    if (entityData.hasComponents(spell, AttackPointsPerTurnComponent.class)) {
      AttackPointsPerTurnComponent apPerTurn =
          entityData.getComponent(spell, AttackPointsPerTurnComponent.class);
      statsPerTurn.add(
          String.format(
              "%s-%s AP per turn for %s rounds",
              apPerTurn.getMinValue(), apPerTurn.getMaxValue(), apPerTurn.getDuration()));
    }
    if (entityData.hasComponents(spell, MovementPointsPerTurnComponent.class)) {
      MovementPointsPerTurnComponent mpPerTurn =
          entityData.getComponent(spell, MovementPointsPerTurnComponent.class);
      statsPerTurn.add(
          String.format(
              "%s-%s MP per turn for %s rounds",
              mpPerTurn.getMinValue(), mpPerTurn.getMaxValue(), mpPerTurn.getDuration()));
    }

    return !statsPerTurn.isEmpty() ? "Buffs " + String.join(", ", statsPerTurn) + ". " : "";
  }

  private static String getBuffString(EntityData entityData, int spell) {
    List<String> buffs = new ArrayList<>();
    if (entityData.hasComponents(spell, AttackPointsBuffComponent.class)) {
      AttackPointsBuffComponent apBuff =
          entityData.getComponent(spell, AttackPointsBuffComponent.class);
      buffs.add(apBuff.getBuffAmount() + " AP for " + apBuff.getBuffDuration() + " rounds");
    }
    if (entityData.hasComponents(spell, MovementPointBuffComponent.class)) {
      MovementPointBuffComponent mpBuff =
          entityData.getComponent(spell, MovementPointBuffComponent.class);
      buffs.add(mpBuff.getBuffAmount() + " MP for " + mpBuff.getBuffDuration() + " rounds");
    }
    if (entityData.hasComponents(spell, DamageBuffComponent.class)) {
      DamageBuffComponent dmgBuff = entityData.getComponent(spell, DamageBuffComponent.class);
      buffs.add(dmgBuff.getBuffAmount() + " dmg for " + dmgBuff.getBuffDuration() + " rounds");
    }
    if (entityData.hasComponents(spell, HealBuffComponent.class)) {
      HealBuffComponent healBuff = entityData.getComponent(spell, HealBuffComponent.class);
      buffs.add(healBuff.getBuffAmount() + " heal for " + healBuff.getBuffDuration() + " rounds");
    }
    if (entityData.hasComponents(spell, HealthPointBuffComponent.class)) {
      HealthPointBuffComponent hpBuff =
          entityData.getComponent(spell, HealthPointBuffComponent.class);
      buffs.add(
          hpBuff.getBuffAmount() + " health points for " + hpBuff.getBuffDuration() + " rounds");
    }
    if (entityData.hasComponents(spell, ReflectionBuffComponent.class)) {
      ReflectionBuffComponent reflection =
          entityData.getComponent(spell, ReflectionBuffComponent.class);
      buffs.add(
          reflection.getBuffAmount()
              + " reflection for "
              + reflection.getBuffDuration()
              + " rounds");
    }
    return !buffs.isEmpty() ? "Buffs " + String.join(", ", buffs) + ". " : "";
  }

  private static String getDamageAndHealString(EntityData entityData, int spell) {
    DamageComponent dmg = entityData.getComponent(spell, DamageComponent.class);
    HealComponent heal = entityData.getComponent(spell, HealComponent.class);

    List<String> list = new ArrayList<>();
    if (dmg != null) {
      list.add(getDamageString(dmg));
    }
    if (heal != null) {
      list.add(getHealString(heal));
    }
    String result = String.join(" and ", list);
    return !list.isEmpty() ? result + ". " : "";
  }

  private static String getHealString(HealComponent heal) {
    if (heal == null) {
      return "";
    }

    return heal.isTargetingEnemies()
        ? String.format("Heals all units for %s-%s", heal.getMinHeal(), heal.getMaxHeal())
        : String.format("Heals all enemy units for %s-%s", heal.getMinHeal(), heal.getMaxHeal());
  }

  private static String getDamageString(DamageComponent dmg) {
    if (dmg == null) {
      return "";
    }

    return dmg.isTargetingAllies()
        ? String.format("Damages all units for %s-%s", dmg.getMinDmg(), dmg.getMaxDmg())
        : String.format("Damages all enemy units for %s-%s", dmg.getMinDmg(), dmg.getMaxDmg());
  }

  private static String getCostsAndRange(EntityData entityData, int spell) {
    CostComponent cost = entityData.getComponent(spell, CostComponent.class);

    List<String> list = new ArrayList<>();
    if (cost.getApCost() > 0) {
      list.add(cost.getApCost() + " AP");
    }
    if (cost.getMpCost() > 0) {
      list.add(cost.getMpCost() + " MP");
    }
    if (cost.getHpCost() > 0) {
      list.add(cost.getHpCost() + " HP");
    }
    String costString = String.join(" | ", list);
    return "[" + costString + "] ";
  }
}
