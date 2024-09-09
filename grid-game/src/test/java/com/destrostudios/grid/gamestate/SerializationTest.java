package com.destrostudios.grid.gamestate;

import com.destrostudios.grid.GridGame;
import com.destrostudios.grid.components.Component;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.shared.StartGameInfo;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SerializationTest {

  @Test
  public void testInitializeFromString() {
    GridGame game = new GridGame();
    game.initGame(StartGameInfo.getTestGameInfo());
    String serialized = game.getState();
    GridGame deserialized = new GridGame();
    deserialized.initializeGame(serialized);

    assertEntityWorldEquals(game, deserialized);
  }

  @Test
  public void testInitializeFromEntityWorld() {
    GridGame game = new GridGame();
    game.initGame(StartGameInfo.getTestGameInfo());
    GridGame copy = new GridGame();
    copy.initializeGame(game.getWorld());

    assertEntityWorldEquals(game, copy);
  }

  private void assertEntityWorldEquals(GridGame actual, GridGame expected) {
    EntityWorld actualWorld = actual.getWorld();
    EntityWorld expectedWorld = expected.getWorld();
    assertEquals(expectedWorld.getNextEntity(), actualWorld.getNextEntity());
    for (int entity = 0; entity < expectedWorld.getNextEntity(); entity++) {
      List<Component> expectedComponents = expectedWorld.getComponents(entity);
      List<Component> actualComponents = actualWorld.getComponents(entity);
      // Returned component order is allowed to differ (allows performance improvements), as the game logic doesn't rely on it
      assertEquals(expectedComponents.size(), actualComponents.size());
      assertTrue(actualComponents.containsAll(expectedComponents));
    }
  }
}
