package com.destrostudios.grid.serialization;

import com.destrostudios.grid.components.character.PlayerComponent;
import com.destrostudios.grid.components.map.ObstacleComponent;
import com.destrostudios.grid.components.map.PositionComponent;
import com.destrostudios.grid.components.map.StartingFieldComponent;
import com.destrostudios.grid.components.map.VisualComponent;
import com.destrostudios.grid.components.map.WalkableComponent;
import com.destrostudios.grid.components.properties.BuffsComponent;
import com.destrostudios.grid.components.properties.MaxAttackPointsComponent;
import com.destrostudios.grid.components.properties.MaxHealthComponent;
import com.destrostudios.grid.components.properties.MaxMovementPointsComponent;
import com.destrostudios.grid.components.properties.NameComponent;
import com.destrostudios.grid.components.properties.SpellsComponent;
import com.destrostudios.grid.components.properties.StatsPerRoundComponent;
import com.destrostudios.grid.components.properties.resistance.AttackPointResistanceComponent;
import com.destrostudios.grid.components.properties.resistance.MovementPointResistanceComponent;
import com.destrostudios.grid.components.spells.base.DamageComponent;
import com.destrostudios.grid.components.spells.base.TooltipComponent;
import com.destrostudios.grid.components.spells.buffs.AttackPointsBuffComponent;
import com.destrostudios.grid.components.spells.buffs.HealthPointBuffComponent;
import com.destrostudios.grid.components.spells.buffs.MovementPointBuffComponent;
import com.destrostudios.grid.components.spells.limitations.CooldownComponent;
import com.destrostudios.grid.components.spells.limitations.CostComponent;
import com.destrostudios.grid.components.spells.movements.DisplacementComponent;
import com.destrostudios.grid.components.spells.movements.TeleportComponent;
import com.destrostudios.grid.components.spells.perturn.AttackPointsPerTurnComponent;
import com.destrostudios.grid.components.spells.perturn.CastsPerTurnComponent;
import com.destrostudios.grid.components.spells.perturn.DamagePerTurnComponent;
import com.destrostudios.grid.components.spells.perturn.HealPerTurnComponent;
import com.destrostudios.grid.components.spells.range.AffectedAreaComponent;
import com.destrostudios.grid.components.spells.range.AffectedAreaIndicator;
import com.destrostudios.grid.components.spells.range.RangeComponent;
import com.destrostudios.grid.components.spells.range.RangeIndicator;
import com.destrostudios.grid.entities.EntityData;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.destrostudios.grid.GridGame.MAP_X;
import static com.destrostudios.grid.GridGame.MAP_Y;
import static com.destrostudios.grid.GridGame.MAX_AP;
import static com.destrostudios.grid.GridGame.MAX_HEALTH;
import static com.destrostudios.grid.GridGame.MAX_MP;

public class SampleDataGenarator {

    public static void initTestMap(EntityData data) {
        // add walkables & startingFields
        int startingFields = 15;
        String terrain = getTerrain();
        for (int x = 0; x < MAP_X; x++) {
            for (int y = 0; y < MAP_Y; y++) {
                // add walkable component
                if (Math.random() > 0.2) {
                    int fieldComponent = data.createEntity();
                    data.addComponent(fieldComponent, new WalkableComponent());
                    data.addComponent(fieldComponent, new VisualComponent(terrain));
                    data.addComponent(fieldComponent, new PositionComponent(x, y));
                    if (Math.random() > 0.5 && startingFields > 0) {
                        data.addComponent(fieldComponent, new StartingFieldComponent());
                        startingFields--;
                    }
                }
            }
        }
        // add obstacles
        for (int x = 0; x < MAP_X; x++) {
            for (int y = 0; y < MAP_Y; y++) {
                List<Integer> walkableEntities = data.list(WalkableComponent.class);
                PositionComponent pos = new PositionComponent(x, y);
                boolean isWalkableAndNoStartingField = walkableEntities.stream()
                        .filter(e -> !data.hasComponents(e, StartingFieldComponent.class))
                        .anyMatch(e -> data.getComponent(e, PositionComponent.class).equals(pos));

                if (isWalkableAndNoStartingField && Math.random() < 0.2) {
                    int treeComponent = data.createEntity();
                    data.addComponent(treeComponent, new PositionComponent(x, y));
                    data.addComponent(treeComponent, new VisualComponent(Math.random() > 0.5 ? "tree" : "rock"));
                    data.addComponent(treeComponent, new ObstacleComponent());
                }
            }
        }
    }


    private static String getTerrain() {
        double random = Math.random();
        if (random < 0.33) {
            return "grass";
        } else if (random < 0.66) {
            return "sand";
        }
        return "snow";
    }

    public static void initTestCharacter(EntityData data, String charackterName) {
        List<Integer> spells = new ArrayList<>();
        Random rand = new Random();
        int attackPoints = Math.max(MAX_AP / 2, rand.nextInt(MAX_AP));
        int movementPoints = Math.max(MAX_MP / 2, rand.nextInt(MAX_AP));
        int playerEntity = data.createEntity();

        // dmg spells
        int spell = data.createEntity();
        addDmgSpell(data, rand, attackPoints, spell, AffectedAreaIndicator.LINE);
        spells.add(spell);

        int spell2 = data.createEntity();
        addDmgSpell(data, rand, attackPoints, spell2, AffectedAreaIndicator.SQUARE);
        spells.add(spell2);

        int spell3 = data.createEntity();
        addDmgSpell(data, rand, attackPoints, spell3, AffectedAreaIndicator.PLUS);
        spells.add(spell3);


        // dmg spell + mp buff
        int spell4 = data.createEntity();
        if (Math.random() > 0.7) {
            addDmgSpellWithMpBuff(data, rand, attackPoints, spell4);
        } else {
            addTeleport(data, rand, attackPoints, spell4);
        }
        spells.add(spell4);

        // ap buff
        int spell5 = data.createEntity();
        if (Math.random() < 0.5) {
            addApBuff(data, rand, movementPoints, spell5);
        } else {
            addApAndHpPoison(data, rand, attackPoints, spell5, playerEntity);
        }
        spells.add(spell5);

        // health buff
        int spell6 = data.createEntity();
        if (Math.random() < 0.5) {
            addHealthBuff(data, rand, attackPoints, spell6);
        } else if (Math.random() < 0.5) {
            addHeal(data, spell6, playerEntity);
        } else {
            addDisplacement(data, spell6, playerEntity);
        }
        spells.add(spell6);

        data.addComponent(playerEntity, new MaxMovementPointsComponent(movementPoints));
        data.addComponent(playerEntity, new MaxAttackPointsComponent(attackPoints));
        data.addComponent(playerEntity, new AttackPointResistanceComponent(rand.nextInt(50)));
        data.addComponent(playerEntity, new MovementPointResistanceComponent(rand.nextInt(50)));
        data.addComponent(playerEntity, new StatsPerRoundComponent(new ArrayList<>()));
        data.addComponent(playerEntity, new BuffsComponent(new ArrayList<>()));
        data.addComponent(playerEntity, new ObstacleComponent());
        data.addComponent(playerEntity, new PlayerComponent());
        data.addComponent(playerEntity, new VisualComponent(charackterName));
        int health = Math.max(MAX_HEALTH / 2, rand.nextInt(MAX_HEALTH));
        data.addComponent(playerEntity, new MaxHealthComponent(health));
        data.addComponent(playerEntity, new SpellsComponent(spells));
    }

    private static void addDmgSpell(EntityData data, Random rand, int attackPoints, int spell, AffectedAreaIndicator indicator) {

        String spellName = getSpellName();
        int apCost = Math.max(2, rand.nextInt(attackPoints));
        data.addComponent(spell, new CostComponent(apCost, 0, 0));
        int dmg = Math.max(25, rand.nextInt(50));
        int range = 6;
        data.addComponent(spell, new DamageComponent(dmg, dmg + 50));
        data.addComponent(spell, new NameComponent(spellName));
        data.addComponent(spell, new BuffsComponent(new ArrayList<>()));
        data.addComponent(spell, new TooltipComponent(String.format("OP spell doing %s damage for %s AP in %s", Math.abs(dmg), apCost, indicator)));
        data.addComponent(spell, new CastsPerTurnComponent(3, 0));
        data.addComponent(spell, new RangeComponent(RangeIndicator.LINE_OF_SIGHT, 1, range));
        data.addComponent(spell, new AffectedAreaComponent(indicator, 0, 4));
    }

    private static void addDmgSpellWithMpBuff(EntityData data, Random rand, int attackPoints, int dmgMpSpell) {
        int apCost = Math.max(2, rand.nextInt(attackPoints));
        int dmg = Math.max(25, rand.nextInt(50));
        int mpBuff = Math.max(1, rand.nextInt(2));
        int range = Math.max(3, rand.nextInt(6));
        String spellName = getSpellName();
        data.addComponent(dmgMpSpell, new CostComponent(apCost, 0, 0));
        data.addComponent(dmgMpSpell, new BuffsComponent(new ArrayList<>()));
        data.addComponent(dmgMpSpell, new DamageComponent(dmg, dmg + 30));
        data.addComponent(dmgMpSpell, new NameComponent(spellName));
        data.addComponent(dmgMpSpell, new MovementPointBuffComponent(mpBuff, 1, false));
        data.addComponent(dmgMpSpell, new RangeComponent(RangeIndicator.ALL, 1, range));
        data.addComponent(dmgMpSpell, new CastsPerTurnComponent(2, 0));
        data.addComponent(dmgMpSpell, new TooltipComponent(String.format("Dmg spell doing %s dmg for %s AP and buffing %s MP\nRange: %s", dmg, apCost, mpBuff, range)));

    }

    private static void addApBuff(EntityData data, Random rand, int movementPoints, int spellApBuff) {
        int mpCost = Math.max(4, rand.nextInt(movementPoints));
        int apBuff = Math.max(1, rand.nextInt(3));
        int range = Math.max(3, rand.nextInt(6));
        data.addComponent(spellApBuff, new CostComponent(0, mpCost, 0));
        data.addComponent(spellApBuff, new AttackPointsBuffComponent(apBuff, 2, false));
        data.addComponent(spellApBuff, new NameComponent("Buff"));
        data.addComponent(spellApBuff, new BuffsComponent(new ArrayList<>()));
        data.addComponent(spellApBuff, new RangeComponent(RangeIndicator.ALL, 0, 0));
        data.addComponent(spellApBuff, new CastsPerTurnComponent(1, 0));
        data.addComponent(spellApBuff, new TooltipComponent(String.format("Spell buffing %s AP for %s MP \nRange: %s", apBuff, mpCost, range)));
    }

    private static void addTeleport(EntityData data, Random rand, int attackPoints, int spellApBuff) {
        int apCost = Math.max(3, rand.nextInt(attackPoints));
        int range = Math.max(3, rand.nextInt(6));
        data.addComponent(spellApBuff, new TeleportComponent());
        data.addComponent(spellApBuff, new CostComponent(apCost, 0, 0));
        data.addComponent(spellApBuff, new NameComponent("Jump"));
        data.addComponent(spellApBuff, new RangeComponent(RangeIndicator.ALL, 1, range));
        data.addComponent(spellApBuff, new BuffsComponent(new ArrayList<>()));
        data.addComponent(spellApBuff, new TooltipComponent("Teleporting in a range of " + range));
        data.addComponent(spellApBuff, new CastsPerTurnComponent(2, 0));
    }

    private static void addApAndHpPoison(EntityData data, Random rand, int attackPoints, int spell, int playerEntity) {
        int range;
        int apCost = Math.max(2, rand.nextInt(attackPoints));
        range = Math.max(3, rand.nextInt(6));
        data.addComponent(spell, new CostComponent(apCost, 0, 0));
        data.addComponent(spell, new AttackPointsPerTurnComponent(-1, -3, 2, playerEntity));
        data.addComponent(spell, new DamagePerTurnComponent(-50, -100, 3, playerEntity));
        data.addComponent(spell, new NameComponent("Wound"));
        data.addComponent(spell, new BuffsComponent(new ArrayList<>()));
        data.addComponent(spell, new RangeComponent(RangeIndicator.LINE_OF_SIGHT, 1, range));
        data.addComponent(spell, new CastsPerTurnComponent(3, 0));
        data.addComponent(spell, new TooltipComponent("Spell adds 1-3 AP poison for 2 rounds and 50-100 HP poison for 3 rounds"));
    }

    private static void addHealthBuff(EntityData data, Random rand, int attackPoints, int spellMpHealthBuff) {
        int apCost;
        int hpBuff = Math.max(50, rand.nextInt(150));
        int hpBuffDuration = Math.max(3, rand.nextInt(6));
        int cooldown = 3;
        apCost = Math.max(5, rand.nextInt(attackPoints));

        data.addComponent(spellMpHealthBuff, new CostComponent(apCost, 0, 0));
        data.addComponent(spellMpHealthBuff, new HealthPointBuffComponent(hpBuff, hpBuffDuration, false));
        data.addComponent(spellMpHealthBuff, new NameComponent("Twist"));
        data.addComponent(spellMpHealthBuff, new BuffsComponent(new ArrayList<>()));
        data.addComponent(spellMpHealthBuff, new RangeComponent(RangeIndicator.ALL, 0, 0));
        data.addComponent(spellMpHealthBuff, new TooltipComponent(String.format("Spell buffing %s HP for %s AP. \nCD: %s, Range: 0 ", hpBuff, apCost, cooldown)));
        data.addComponent(spellMpHealthBuff, new CooldownComponent(cooldown));
        data.addComponent(spellMpHealthBuff, new CastsPerTurnComponent(1, 0));
    }

    private static void addHeal(EntityData data, int spell, int playerEntity) {
        int apCost = Math.max(2, 3);
        data.addComponent(spell, new CostComponent(apCost, 0, 0));
        data.addComponent(spell, new HealPerTurnComponent(50, 100, 4, playerEntity));
        data.addComponent(spell, new NameComponent("Blblbl"));
        data.addComponent(spell, new RangeComponent(RangeIndicator.ALL, 0, 3));
        data.addComponent(spell, new CastsPerTurnComponent(3, 0));
        data.addComponent(spell, new BuffsComponent(new ArrayList<>()));
        data.addComponent(spell, new TooltipComponent(String.format("Spell heals 50-100 hp for %s rounds", 4)));
    }

    private static void addDisplacement(EntityData data, int spell, int playerEntity) {
        int apCost = 4;
        data.addComponent(spell, new CostComponent(apCost, 0, 0));
        data.addComponent(spell, new NameComponent(getSpellName()));
        data.addComponent(spell, new CastsPerTurnComponent(3, 0));
        data.addComponent(spell, new RangeComponent(RangeIndicator.LINE_OF_SIGHT, 1, 1));
        data.addComponent(spell, new BuffsComponent(new ArrayList<>()));
        data.addComponent(spell, new DisplacementComponent(5, false));
        data.addComponent(spell, new TooltipComponent(String.format("Displaces player 5 positions for 4 AP")));
    }

    private static String getSpellName() {
        List<String> spellName = Lists.newArrayList("Arrow", "Hit", "Jump", "Twist", "Confusion",
                "Nothing", "Blblbl", "Wound");
        Random random = new Random();
        return spellName.get(random.nextInt(spellName.size()));
    }
}
