package com.destrostudios.grid.gamestate;

import com.destrostudios.grid.GridGame;
import com.destrostudios.grid.shared.StartGameInfo;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SerializationTest {

    @Test
    public void testGameSerialization() {
        GridGame game = new GridGame();
        game.initGame(StartGameInfo.getTestGameInfo());
        String serialized = game.getState();
        GridGame deserialized = new GridGame();
        deserialized.intializeGame(serialized);

        assertEquals(game.getWorld(), deserialized.getWorld());
    }
}
