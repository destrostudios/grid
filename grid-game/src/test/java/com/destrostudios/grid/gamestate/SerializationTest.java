package com.destrostudios.grid.gamestate;

import com.destrostudios.grid.GridGame;
import com.destrostudios.grid.components.Component;
import com.destrostudios.grid.shared.StartGameInfo;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class SerializationTest {

    @Test
    public void testGameSerialization() {
        GridGame game = new GridGame();
        game.initGame(StartGameInfo.getTestGameInfo());
        String serialized = game.getState();
        GridGame deserialized = new GridGame();
        deserialized.intializeGame(serialized);

        // TODO: we should use the high level methods on EntityWorld instead of using its map, the map is an implementation detail.
        Map<Integer, List<Component>> gameMap = game.getComponents();
        Map<Integer, List<Component>> deserializedMap = deserialized.getComponents();
        assertEquals(gameMap.keySet(), deserializedMap.keySet());
        for (Map.Entry<Integer, List<Component>> entry : gameMap.entrySet()) {
            Integer entity = entry.getKey();
            List<Component> entityComponents = entry.getValue();
            List<Component> deserializedEntityComponents = deserializedMap.get(entity);
            assertNotNull(deserializedEntityComponents);
            String message = "expected: " + entityComponents + "\nactual: " + deserializedEntityComponents;
            assertFalse(message, deserializedEntityComponents.contains(null));
            assertEquals(message, entityComponents.size(), deserializedEntityComponents.size());
            // TODO: assert that components are equal (components currently don't implement equals)
        }
    }
}
