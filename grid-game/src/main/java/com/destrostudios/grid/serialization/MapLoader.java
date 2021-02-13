package com.destrostudios.grid.serialization;

import com.destrostudios.grid.components.*;
import com.destrostudios.grid.entities.EntityWorld;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.destrostudios.grid.GridGame.MAP_X;
import static com.destrostudios.grid.GridGame.MAP_Y;

public class MapLoader {
    public static final String MAPS = "maps/";
    public static final String JSON = ".json";
    public static String PATH = "D:\\Workspace\\Grid\\grid-game\\src\\main\\resources\\maps";

//    public static void main(String[] args) throws JsonProcessingException {
//        GridGame gridGame = new GridGame();
//        MapLoader.createAndSaveRandoMap("DestroMap", gridGame.getWorld());
//    }

    public static void createAndSaveRandoMap(String mapName, EntityWorld world) {
        initRandomMap(world);
        saveMap(mapName, world);
    }

    private static void initRandomMap(EntityWorld world) {
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

    private static void saveMap(String mapName, EntityWorld world) {
        List<Integer> entities = new ArrayList<>();
        entities.addAll(world.list(WalkableComponent.class));
        entities.addAll(world.list(ObstacleComponent.class));

        GridMap gridMap = new GridMap();
        Map<Integer, List<Component>> map = new LinkedHashMap<>();
        for (Integer entity : entities) {
            List<Component> components = world.getComponents(entity);
            map.put(entity, components);
        }
        gridMap.setMap(map);
        try {
            writeMapToResources(gridMap, mapName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<Integer, List<Component>> readMap() {
        try {
            return readMapFromRessources("DestroMap").getMap();
        } catch (IOException e) {
            // TODO: 13.02.2021 logger
        }
        return new GridMap().getMap();
    }

    public static GridMap readMapFromRessources(String mapName) throws IOException {
        InputStream is = GridMap.class.getClassLoader().getResourceAsStream(MAPS + mapName + JSON);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(is, GridMap.class);
    }

    public static void writeMapToResources(GridMap map, String mapName) throws IOException {
        Path patha = Path.of(PATH);
//        String path = map.getClass().getResource("/resources/").getPath();
        File file = new File(patha.toFile(), mapName + ".json");
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(file, map);
    }
}
