package com.destrostudios.grid.serialization;

import com.destrostudios.grid.GridGame;
import com.destrostudios.grid.components.Component;
import com.destrostudios.grid.components.character.PlayerComponent;
import com.destrostudios.grid.components.map.*;
import com.destrostudios.grid.components.properties.*;
import com.destrostudios.grid.components.spells.*;
import com.destrostudios.grid.components.spells.buffs.AttackPointsBuffComponent;
import com.destrostudios.grid.components.spells.buffs.HealthPointBuffComponent;
import com.destrostudios.grid.components.spells.buffs.MovementPointBuffComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.serialization.container.CharacterContainer;
import com.destrostudios.grid.serialization.container.ComponentsContainer;
import com.destrostudios.grid.serialization.container.GameStateContainer;
import com.destrostudios.grid.serialization.container.MapContainer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.*;

import static com.destrostudios.grid.GridGame.*;

public class ComponentsContainerSerializer {
    public static final String CHARACTER = "/character/";
    public static final String MAPS = "/maps/";
    public static final String JSON = ".json";
    public static String BASE_PATH = "D:/Workspace/Grid/grid-game/src/main/resources";

//    public static void main(String[] args) throws JsonProcessingException {
//        generateAndSaveMap("DestroMap");
//        generateAndSaveMap("EtherMap");
//        generateAndSaveMap("IceMap");
//    }

    public static void main(String[] args) throws JsonProcessingException {
        generateAndSaveCharacter("destroflyer");
        generateAndSaveCharacter("Etherblood");
        generateAndSaveCharacter("Icecold");
    }

    public static <E extends ComponentsContainer> E readContainerAsJson(String gameState, Class<E> classz) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter();
        return mapper.readValue(gameState, classz);
    }

    public static String getContainerAsJson(EntityWorld world) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter();
        GameStateContainer gameStateContainer = new GameStateContainer(world.getWorld());
        return mapper.writeValueAsString(gameStateContainer);
    }

    public static void generateAndSaveMap(String name) {
        GridGame gridGame = new GridGame();
        ComponentsContainerSerializer.initTestMap(gridGame.getWorld());
        Map<Integer, List<Component>> components = ComponentsContainerSerializer.getComponents(gridGame.getWorld(), MapContainer.class);
        try {
            ComponentsContainerSerializer.writeSeriazableToResources(new MapContainer(components), name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void generateAndSaveCharacter(String name) {
        GridGame gridGame = new GridGame();
        ComponentsContainerSerializer.initTestCharacter(gridGame.getWorld(), name);
        Map<Integer, List<Component>> components = ComponentsContainerSerializer.getComponents(gridGame.getWorld(), CharacterContainer.class);
        try {
            ComponentsContainerSerializer.writeSeriazableToResources(new CharacterContainer(components), name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void initTestCharacter(EntityWorld world, String name) {
        List<Integer> spells = new ArrayList<>();
        Random rand = new Random();
        int attackPoints = Math.max(MAX_AP / 2, rand.nextInt(MAX_AP));
        int movementPoints = Math.max(MAX_MP / 2, rand.nextInt(MAX_AP));


        // dmg spells
        for (int i = 0; i < 2; i++) {
            int spell = world.createEntity();
            String spellName = getSpellName(name);
            int apCost = Math.max(2, rand.nextInt(attackPoints));
            world.addComponent(spell, new AttackPointCostComponent(apCost));
            int dmg = Math.max(25, rand.nextInt(50));
            int range = Math.max(3, rand.nextInt(6));
            world.addComponent(spell, new DamageComponent(dmg));
            world.addComponent(spell, new NameComponent(spellName));
            world.addComponent(spell, new TooltipComponent(String.format("OP spell doing %s damage for %s AP", dmg, apCost)));
            world.addComponent(spell, new RangeComponent(range));
            spells.add(spell);
        }
        // dmg spell + bp buff
        int dmgMpSpell = world.createEntity();
        int apCost = Math.max(2, rand.nextInt(attackPoints));
        int dmg = Math.max(25, rand.nextInt(50));
        int mpBuff = Math.max(1, rand.nextInt(2));
        int range = Math.max(3, rand.nextInt(6));
        String spellName = getSpellName(name);
        world.addComponent(dmgMpSpell, new AttackPointCostComponent(apCost));
        world.addComponent(dmgMpSpell, new DamageComponent(dmg));
        world.addComponent(dmgMpSpell, new NameComponent(spellName));
        world.addComponent(dmgMpSpell, new MovementPointBuffComponent(mpBuff, 1));
        world.addComponent(dmgMpSpell, new RangeComponent(range));
        world.addComponent(dmgMpSpell, new TooltipComponent(String.format("Dmg spell doing %s dmg for %s AP and buffing %s MP\nRange: %s", dmg, apCost, mpBuff, range)));
        spells.add(dmgMpSpell);

        // ap buff
        int spellApBuff = world.createEntity();
        int mpCost = Math.max(4, rand.nextInt(movementPoints));
        int apBuff = Math.max(1, rand.nextInt(3));
        range = Math.max(3, rand.nextInt(6));
        world.addComponent(spellApBuff, new MovementPointsCostComponent(mpCost));
        world.addComponent(spellApBuff, new AttackPointsBuffComponent(apBuff, 2));
        world.addComponent(spellApBuff, new NameComponent(name + "Buff"));
        world.addComponent(spellApBuff, new RangeComponent(range));
        world.addComponent(spellApBuff, new TooltipComponent(String.format("Spell buffing %s AP for %s MP \nRange: %s", apBuff, mpCost, range)));
        spells.add(spellApBuff);

        // health buff
        int spellMpHealthBuff = world.createEntity();
        int hpBuff = Math.max(50, rand.nextInt(150));
        int hpBuffDuration = Math.max(3, rand.nextInt(6));
        int cooldown = 3;
        apCost = Math.max(5, rand.nextInt(attackPoints));
        range = Math.max(3, rand.nextInt(6));
        world.addComponent(spellMpHealthBuff, new AttackPointCostComponent(apCost));
        world.addComponent(spellMpHealthBuff, new HealthPointBuffComponent(hpBuff, hpBuffDuration));
        world.addComponent(spellMpHealthBuff, new NameComponent(name + "Pump"));
        world.addComponent(spellMpHealthBuff, new RangeComponent(0));
        world.addComponent(spellMpHealthBuff, new TooltipComponent(String.format("Spell buffing %s HP for %s AP. \nCD: %s, Range: 0 ", hpBuff, apCost, cooldown)));
        world.addComponent(spellMpHealthBuff, new CooldownComponent(cooldown));
        spells.add(spellMpHealthBuff);

        int playerEntity = world.createEntity();
        world.addComponent(playerEntity, new MaxMovementPointsComponent(movementPoints));
        world.addComponent(playerEntity, new MaxAttackPointsComponent(attackPoints));
        world.addComponent(playerEntity, new ObstacleComponent());
        world.addComponent(playerEntity, new PlayerComponent());
        world.addComponent(playerEntity, new NameComponent(name));
        int health = Math.max(MAX_HEALTH / 2, rand.nextInt(MAX_HEALTH));
        world.addComponent(playerEntity, new MaxHealthComponent(health));
        world.addComponent(playerEntity, new SpellsComponent(spells));
    }

    private static String getSpellName(String name) {
        List<String> spellName = Lists.newArrayList("Bomb", "Arrow", "Punch", "Hit", "Jump", "Twist", "Confusion",
                "Nothing", "Blblbl", "Wound", "");
        Random random = new Random();
        return name + spellName.get(random.nextInt(spellName.size()));
    }

    private static void initTestMap(EntityWorld world) {
        // add walkables & startingFields
        int startingFields = 15;
        for (int x = 0; x < MAP_X; x++) {
            for (int y = 0; y < MAP_Y; y++) {
                // add walkable component
                if (Math.random() > 0.2) {
                    int fieldComponent = world.createEntity();
                    world.addComponent(fieldComponent, new WalkableComponent());
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
                    world.addComponent(treeComponent, new TreeComponent());
                    world.addComponent(treeComponent, new ObstacleComponent());
                }
            }
        }
    }

    public static <E extends ComponentsContainer> E readSeriazableFromRessources(String seriazableName, Class<E> classz) throws IOException {
        InputStream is = ComponentsContainer.class.getResourceAsStream(getPath(classz) + seriazableName + JSON);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(is, classz);
    }

    public static <E extends ComponentsContainer> void writeSeriazableToResources(E seriazableComponents, String seriazableName) throws IOException {
        Path path = Path.of(BASE_PATH + getPath(seriazableComponents.getClass()));
        File file = new File(path.toFile(), seriazableName + ".json");
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(file, seriazableComponents);
    }

    private static String getPath(Class<? extends ComponentsContainer> seriazableComponentsClass) {
        if (seriazableComponentsClass.equals(CharacterContainer.class)) {
            return CHARACTER;
        } else if (seriazableComponentsClass.equals(MapContainer.class)) {
            return MAPS;
        }
        return "";
    }

    private static Map<Integer, List<Component>> getComponents(EntityWorld world, Class<? extends ComponentsContainer> seriazableComponentsClass) {
        Map<Integer, List<Component>> result = new LinkedHashMap<>();

        if (seriazableComponentsClass.equals(CharacterContainer.class)) {
            Optional<Integer> champOpt = world.list(PlayerComponent.class).stream()
                    .findFirst();

            // 1. add all character components
            List<Component> champComponents = world.getComponents(champOpt.get());
            result.put(champOpt.get(), champComponents);
            // 2. add all spells of that character
            Optional<SpellsComponent> allSpellsFromChamp = champComponents.stream()
                    .filter(c -> c instanceof SpellsComponent)
                    .map(c -> (SpellsComponent) c)
                    .findFirst();
            allSpellsFromChamp.get().getSpells().forEach(spell -> result.put(spell, world.getComponents(spell)));

        } else if (seriazableComponentsClass.equals(MapContainer.class)) {
            List<Integer> entities = new ArrayList<>();
            entities.addAll(world.list(WalkableComponent.class));
            entities.addAll(world.list(ObstacleComponent.class));

            for (Integer entity : entities) {
                List<Component> components = world.getComponents(entity);
                result.put(entity, components);
            }
        }
        return result;
    }
}
