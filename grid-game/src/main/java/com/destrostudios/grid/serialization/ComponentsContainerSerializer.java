package com.destrostudios.grid.serialization;

import com.destrostudios.grid.GridGame;
import com.destrostudios.grid.components.Component;
import com.destrostudios.grid.components.character.PlayerComponent;
import com.destrostudios.grid.components.map.ObstacleComponent;
import com.destrostudios.grid.components.map.WalkableComponent;
import com.destrostudios.grid.components.properties.SpellsComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.serialization.container.CharacterContainer;
import com.destrostudios.grid.serialization.container.ComponentsContainer;
import com.destrostudios.grid.serialization.container.GameStateContainer;
import com.destrostudios.grid.serialization.container.MapContainer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.*;

import static com.destrostudios.grid.serialization.SampleDataGenarator.initTestCharacter;
import static com.destrostudios.grid.serialization.SampleDataGenarator.initTestMap;

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
        generateAndSaveCharacter("aland");
        generateAndSaveCharacter("alice");
        generateAndSaveCharacter("dosaz");
        generateAndSaveCharacter("dwarf_warrior");
        generateAndSaveCharacter("elven_archer");
        generateAndSaveCharacter("garmon");
        generateAndSaveCharacter("scarlet");
        generateAndSaveCharacter("tristan");
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


    public static void generateAndSaveCharacter(String characterName) {
        GridGame gridGame = new GridGame();
        initTestCharacter(gridGame.getWorld(), characterName);
        Map<Integer, List<Component>> components = ComponentsContainerSerializer.getComponents(gridGame.getWorld(), CharacterContainer.class);
        try {
            ComponentsContainerSerializer.writeSeriazableToResources(new CharacterContainer(components), characterName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void generateAndSaveMap(String name) {
        GridGame gridGame = new GridGame();
        initTestMap(gridGame.getWorld());
        Map<Integer, List<Component>> components = ComponentsContainerSerializer.getComponents(gridGame.getWorld(), MapContainer.class);
        try {
            ComponentsContainerSerializer.writeSeriazableToResources(new MapContainer(components), name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Players: aland, alice, dosaz, dwarf_warrior, elven_archer, garmon, scarlet, tristan
    private static String getCharacterName() {
        double random = Math.random();
        if (random < 0.2) {
            return "aland";
        } else if (random < 0.4) {
            return "alice";
        } else if (random < 0.6) {
            return "dosaz";
        } else if (random < 0.8) {
            return "elven_archer";
        }
        return "dwarf_warrior";
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
