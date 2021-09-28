package com.destrostudios.grid.characters;

import com.destrostudios.grid.GridGame;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class CharacterIntegrationTest {

  protected GridGame game;
  protected String testingCharacter;

  @BeforeAll
  protected void before() {
    this.game = new GridGame();
  }
}
