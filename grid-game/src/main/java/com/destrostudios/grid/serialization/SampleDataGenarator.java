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
import com.destrostudios.grid.components.properties.resistence.AttackPointResistenceComponent;
import com.destrostudios.grid.components.properties.resistence.MovementPointResistenceComponent;
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
import com.destrostudios.grid.components.spells.range.RangeComponent;
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

    public static void initTestMap(EntityData world) {
        // add walkables & startingFields
        int startingFields = 15;
        String terrain = getTerrain();
        for (int x = 0; x < MAP_X; x++) {
            for (int y = 0; y < MAP_Y; y++) {
                // add walkable component
                if (Math.random() > 0.2) {
                    int fieldComponent = world.createEntity();
                    world.addComponent(fieldComponent, new WalkableComponent());
                    world.addComponent(fieldComponent, new VisualComponent(terrain));
                    world.addComponent(fieldComponent, new PositionComponent(x, y));
                    if (Math.random() > 0.5 && startingFields > 0) {
                        world.addComponent(fieldComponent, new StartingFieldComponent());
                        startingFields--;
                    }
                }
            }
        }
        // add obstacles
        for (int x = 0; x < MAP_X; x++) {
            for (int y = 0; y < MAP_Y; y++) {
                List<Integer> walkableEntities = world.list(WalkableComponent.class);
                PositionComponent pos = new PositionComponent(x, y);
                boolean isWalkableAndNoStartingField = walkableEntities.stream()
                        .filter(e -> !world.hasComponents(e, StartingFieldComponent.class))
                        .anyMatch(e -> world.getComponent(e, PositionComponent.class).equals(pos));

                if (isWalkableAndNoStartingField && Math.random() < 0.2) {
                    int treeComponent = world.createEntity();
                    world.addComponent(treeComponent, new PositionComponent(x, y));
                    world.addComponent(treeComponent, new VisualComponent(Math.random() > 0.5 ? "tree" : "rock"));
                    world.addComponent(treeComponent, new ObstacleComponent());
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

    public static void initTestCharacter(EntityData world, String charackterName) {
        List<Integer> spells = new ArrayList<>();
        Random rand = new Random();
        int attackPoints = Math.max(MAX_AP / 2, rand.nextInt(MAX_AP));
        int movementPoints = Math.max(MAX_MP / 2, rand.nextInt(MAX_AP));
        int playerEntity = world.createEntity();

        // dmg spells
        for (int i = 0; i < 2; i++) {
            int spell = world.createEntity();
            addDmgSpell(world, rand, attackPoints, spell);
            spells.add(spell);
        }

        // dmg spell + mp buff
        int spell4 = world.createEntity();
        if (Math.random() > 0.7) {
            addDmgSpellWithMpBuff(world, rand, attackPoints, spell4);
        } else {
            addTeleport(world, rand, attackPoints, spell4);
        }
        spells.add(spell4);

        // ap buff
        int spell5 = world.createEntity();
        if (Math.random() < 0.5) {
            addApBuff(world, rand, movementPoints, spell5);
        } else {
            addApAndHpPoison(world, rand, attackPoints, spell5, playerEntity);
        }
        spells.add(spell5);

        // health buff
        int spell6 = world.createEntity();
        if (Math.random() < 0.5) {
            addHealthBuff(world, rand, attackPoints, spell6);
        } else if (Math.random() < 0.5) {
            addHeal(world, spell6, playerEntity);
        } else if (Math.random() < 0.5) {
            addDisplacement(world, spell6, playerEntity);
        }
        spells.add(spell6);

        world.addComponent(playerEntity, new MaxMovementPointsComponent(movementPoints));
        world.addComponent(playerEntity, new MaxAttackPointsComponent(attackPoints));
        world.addComponent(playerEntity, new AttackPointResistenceComponent(rand.nextInt(50)));
        world.addComponent(playerEntity, new MovementPointResistenceComponent(rand.nextInt(50)));
        world.addComponent(playerEntity, new StatsPerRoundComponent(new ArrayList<>()));
        world.addComponent(playerEntity, new BuffsComponent(new ArrayList<>()));
        world.addComponent(playerEntity, new ObstacleComponent());
        world.addComponent(playerEntity, new PlayerComponent());
        world.addComponent(playerEntity, new VisualComponent(charackterName));
        int health = Math.max(MAX_HEALTH / 2, rand.nextInt(MAX_HEALTH));
        world.addComponent(playerEntity, new MaxHealthComponent(health));
        world.addComponent(playerEntity, new SpellsComponent(spells));
    }

    private static void addDmgSpell(EntityData world, Random rand, int attackPoints, int spell) {
        String spellName = getSpellName();
        int apCost = Math.max(2, rand.nextInt(attackPoints));
        world.addComponent(spell, new CostComponent(apCost, 0, 0));
        int dmg = Math.max(25, rand.nextInt(50));
        int range = Math.max(3, rand.nextInt(6));
        world.addComponent(spell, new DamageComponent(dmg, dmg + 50));
        world.addComponent(spell, new NameComponent(spellName));
        world.addComponent(spell, new BuffsComponent(new ArrayList<>()));
        world.addComponent(spell, new TooltipComponent(String.format("OP spell doing %s damage for %s AP", Math.abs(dmg), apCost)));
        world.addComponent(spell, new CastsPerTurnComponent(3, 0));
        world.addComponent(spell, new RangeComponent(1, range));
    }

    private static void addDmgSpellWithMpBuff(EntityData world, Random rand, int attackPoints, int dmgMpSpell) {
        int apCost = Math.max(2, rand.nextInt(attackPoints));
        int dmg = Math.max(25, rand.nextInt(50));
        int mpBuff = Math.max(1, rand.nextInt(2));
        int range = Math.max(3, rand.nextInt(6));
        String spellName = getSpellName();
        world.addComponent(dmgMpSpell, new CostComponent(apCost, 0, 0));
        world.addComponent(dmgMpSpell, new BuffsComponent(new ArrayList<>()));
        world.addComponent(dmgMpSpell, new DamageComponent(dmg, dmg + 30));
        world.addComponent(dmgMpSpell, new NameComponent(spellName));
        world.addComponent(dmgMpSpell, new MovementPointBuffComponent(mpBuff, 1, false));
        world.addComponent(dmgMpSpell, new RangeComponent(1, range));
        world.addComponent(dmgMpSpell, new CastsPerTurnComponent(2, 0));
        world.addComponent(dmgMpSpell, new TooltipComponent(String.format("Dmg spell doing %s dmg for %s AP and buffing %s MP\nRange: %s", dmg, apCost, mpBuff, range)));
    }

    private static void addApBuff(EntityData world, Random rand, int movementPoints, int spellApBuff) {
        int mpCost = Math.max(4, rand.nextInt(movementPoints));
        int apBuff = Math.max(1, rand.nextInt(3));
        int range = Math.max(3, rand.nextInt(6));
        world.addComponent(spellApBuff, new CostComponent(0, mpCost, 0));
        world.addComponent(spellApBuff, new AttackPointsBuffComponent(apBuff, 2, false));
        world.addComponent(spellApBuff, new NameComponent("Buff"));
        world.addComponent(spellApBuff, new BuffsComponent(new ArrayList<>()));
        world.addComponent(spellApBuff, new RangeComponent(0, 0));
        world.addComponent(spellApBuff, new CastsPerTurnComponent(1, 0));
        world.addComponent(spellApBuff, new TooltipComponent(String.format("Spell buffing %s AP for %s MP \nRange: %s", apBuff, mpCost, range)));
    }

    private static void addTeleport(EntityData world, Random rand, int attackPoints, int spellApBuff) {
        int apCost = Math.max(3, rand.nextInt(attackPoints));
        int range = Math.max(3, rand.nextInt(6));
        world.addComponent(spellApBuff, new TeleportComponent());
        world.addComponent(spellApBuff, new CostComponent(apCost, 0, 0));
        world.addComponent(spellApBuff, new NameComponent("Jump"));
        world.addComponent(spellApBuff, new RangeComponent(1, range));
        world.addComponent(spellApBuff, new BuffsComponent(new ArrayList<>()));
        world.addComponent(spellApBuff, new TooltipComponent("Teleporting in a range of " + range));
        world.addComponent(spellApBuff, new CastsPerTurnComponent(2, 0));
    }

    private static void addApAndHpPoison(EntityData world, Random rand, int attackPoints, int spell, int playerEntity) {
        int range;
        int apCost = Math.max(2, rand.nextInt(attackPoints));
        range = Math.max(3, rand.nextInt(6));
        world.addComponent(spell, new CostComponent(apCost, 0, 0));
        world.addComponent(spell, new AttackPointsPerTurnComponent(-1, -3, 2, playerEntity));
        world.addComponent(spell, new DamagePerTurnComponent(-50, -100, 3, playerEntity));
        world.addComponent(spell, new NameComponent("Wound"));
        world.addComponent(spell, new BuffsComponent(new ArrayList<>()));
        world.addComponent(spell, new RangeComponent(1, range));
        world.addComponent(spell, new CastsPerTurnComponent(3, 0));
        world.addComponent(spell, new TooltipComponent("Spell adds 1-3 AP poison for 2 rounds and 50-100 HP poison for 3 rounds"));
    }

    private static void addHealthBuff(EntityData world, Random rand, int attackPoints, int spellMpHealthBuff) {
        int apCost;
        int hpBuff = Math.max(50, rand.nextInt(150));
        int hpBuffDuration = Math.max(3, rand.nextInt(6));
        int cooldown = 3;
        apCost = Math.max(5, rand.nextInt(attackPoints));

        world.addComponent(spellMpHealthBuff, new CostComponent(apCost, 0, 0));
        world.addComponent(spellMpHealthBuff, new HealthPointBuffComponent(hpBuff, hpBuffDuration, false));
        world.addComponent(spellMpHealthBuff, new NameComponent("Twist"));
        world.addComponent(spellMpHealthBuff, new BuffsComponent(new ArrayList<>()));
        world.addComponent(spellMpHealthBuff, new RangeComponent(0, 0));
        world.addComponent(spellMpHealthBuff, new TooltipComponent(String.format("Spell buffing %s HP for %s AP. \nCD: %s, Range: 0 ", hpBuff, apCost, cooldown)));
        world.addComponent(spellMpHealthBuff, new CooldownComponent(cooldown));
        world.addComponent(spellMpHealthBuff, new CastsPerTurnComponent(1, 0));

    }

    private static void addHeal(EntityData world, int spell, int playerEntity) {
        int apCost = Math.max(2, 3);
        world.addComponent(spell, new CostComponent(apCost, 0, 0));
        world.addComponent(spell, new HealPerTurnComponent(50, 100, 4, playerEntity));
        world.addComponent(spell, new NameComponent("Blblbl"));
        world.addComponent(spell, new RangeComponent(0, 3));
        world.addComponent(spell, new CastsPerTurnComponent(3, 0));
        world.addComponent(spell, new BuffsComponent(new ArrayList<>()));
        world.addComponent(spell, new TooltipComponent(String.format("Spell heals 50-100 hp for %s rounds", 4)));
    }

    private static void addDisplacement(EntityData world, int spell, int playerEntity) {
        int apCost = 4;
        world.addComponent(spell, new CostComponent(apCost, 0, 0));
        world.addComponent(spell, new NameComponent(getSpellName()));
        world.addComponent(spell, new CastsPerTurnComponent(3, 0));
        world.addComponent(spell, new RangeComponent(1, 1));
        world.addComponent(spell, new BuffsComponent(new ArrayList<>()));
        world.addComponent(spell, new DisplacementComponent(5));
        world.addComponent(spell, new TooltipComponent(String.format("Displaces player 5 positions for 4 AP")));
    }

    private static String getSpellName() {
        List<String> spellName = Lists.newArrayList("Arrow", "Hit", "Jump", "Twist", "Confusion",
                "Nothing", "Blblbl", "Wound");
        Random random = new Random();
        return spellName.get(random.nextInt(spellName.size()));
    }
}
