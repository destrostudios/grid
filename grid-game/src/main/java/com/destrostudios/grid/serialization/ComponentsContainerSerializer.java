package com.destrostudios.grid.serialization;

import com.destrostudios.grid.GridGame;
import com.destrostudios.grid.components.Component;
import com.destrostudios.grid.components.character.PlayerComponent;
import com.destrostudios.grid.components.map.*;
import com.destrostudios.grid.components.properties.*;
import com.destrostudios.grid.components.spells.AttackPointCostComponent;
import com.destrostudios.grid.components.spells.DamageComponent;
import com.destrostudios.grid.components.spells.MovementPointsCostComponent;
import com.destrostudios.grid.components.spells.SpellComponent;
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
import java.util.stream.Collectors;

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
            world.addComponent(spell, new AttackPointCostComponent(rand.nextInt(attackPoints)));
            world.addComponent(spell, new DamageComponent(Math.max(25, rand.nextInt(50))));
            world.addComponent(spell, new NameComponent(spellName));
            spells.add(spell);
        }
        // dmg spell + bp buff
        int dmgMpSpell = world.createEntity();
        String spellName = getSpellName(name);
        world.addComponent(dmgMpSpell, new AttackPointCostComponent(rand.nextInt(attackPoints)));
        world.addComponent(dmgMpSpell, new DamageComponent(Math.max(25, rand.nextInt(50))));
        world.addComponent(dmgMpSpell, new NameComponent(spellName));
        world.addComponent(dmgMpSpell, new MovementPointsComponent(Math.max(1, rand.nextInt(2))));
        spells.add(dmgMpSpell);

        // ap buff
        int spellApBuff = world.createEntity();
        world.addComponent(spellApBuff, new MovementPointsCostComponent(Math.max(4, rand.nextInt(movementPoints))));
        world.addComponent(spellApBuff, new AttackPointsComponent(Math.max(1, rand.nextInt(3))));
        world.addComponent(spellApBuff, new NameComponent(name + "Buff"));
        spells.add(spellApBuff);

        // health buff
        int spellMpHealthBuff = world.createEntity();
        world.addComponent(spellMpHealthBuff, new AttackPointCostComponent(Math.max(5, rand.nextInt(attackPoints))));
        world.addComponent(spellMpHealthBuff, new HealthPointsComponent(Math.max(50, rand.nextInt(150))));
        world.addComponent(spellMpHealthBuff, new NameComponent(name + "Pump"));
        spells.add(spellMpHealthBuff);

        int playerEntity = world.createEntity();
        world.addComponent(playerEntity, new MovementPointsComponent(movementPoints));
        world.addComponent(playerEntity, new MaxMovementPointsComponent(movementPoints));
        world.addComponent(playerEntity, new AttackPointsComponent(attackPoints));
        world.addComponent(playerEntity, new MaxAttackPointsComponent(attackPoints));
        world.addComponent(playerEntity, new ObstacleComponent());
        world.addComponent(playerEntity, new PlayerComponent());
        world.addComponent(playerEntity, new NameComponent(name));
        int health = Math.max(MAX_HEALTH / 2, rand.nextInt(MAX_HEALTH));
        world.addComponent(playerEntity, new HealthPointsComponent(health));
        world.addComponent(playerEntity, new MaxHealthComponent(health));

        for (Integer spellEntity : spells) {
            world.addComponent(playerEntity, new SpellComponent(spellEntity));
        }
    }

    private static String getSpellName(String name) {
        List<String> spellName = Lists.newArrayList("Bomb", "Arrow", "Punch", "Hit", "Jump", "Twist", "Confusion", "Nothing", "Blblbl");
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
                        .filter(e -> world.getComponent(e, StartingFieldComponent.class).isEmpty())
                        .anyMatch(e -> world.getComponent(e, PositionComponent.class).get().equals(pos));

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
            List<SpellComponent> allSpellsFromChamp = champComponents.stream()
                    .filter(c -> c instanceof SpellComponent)
                    .map(c -> (SpellComponent) c)
                    .collect(Collectors.toList());
            allSpellsFromChamp.forEach(c -> result.put(c.getSpell(), world.getComponents(c.getSpell())));

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
