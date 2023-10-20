package com.destrostudios.grid.gamestate;

import com.destrostudios.grid.GridGame;
import com.destrostudios.grid.shared.StartGameInfo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SerializationTest {

  @Test
  public void testGameSerialization() {
    GridGame game = new GridGame();
    game.initGame(StartGameInfo.getTestGameInfo());
    String serialized = game.getState();
    GridGame deserialized = new GridGame();
    deserialized.initializeGame(serialized);

    assertEquals(game.getData(), deserialized.getData());
  }
}
