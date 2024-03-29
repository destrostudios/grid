package com.destrostudios.grid.characters;

import com.destrostudios.grid.GridGame;
import com.destrostudios.grid.actions.Action;
import com.destrostudios.grid.actions.CastSpellAction;
import com.destrostudios.grid.actions.SkipRoundAction;
import com.destrostudios.grid.components.Component;
import com.destrostudios.grid.components.character.PlayerComponent;
import com.destrostudios.grid.components.map.PositionComponent;
import com.destrostudios.grid.components.properties.HealthPointsComponent;
import com.destrostudios.grid.components.properties.NameComponent;
import com.destrostudios.grid.components.properties.SpellsComponent;
import com.destrostudios.grid.components.spells.base.DamageComponent;
import com.destrostudios.grid.components.spells.buffs.DamageBuffComponent;
import com.destrostudios.grid.components.spells.limitations.CooldownComponent;
import com.destrostudios.grid.random.RandomProxy;
import com.destrostudios.grid.shared.PlayerInfo;
import com.destrostudios.grid.shared.StartGameInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IopTest {

  GridGame game;
  RandomProxy randomProxy;
  PlayerInfo player1;
  PlayerInfo player2;

  @BeforeEach
  public void init() {
    // TODO: create utility methods to add/remove players/characters and start an empty game instead

    randomProxy = Mockito.mock(RandomProxy.class);
    StartGameInfo startInfo = new StartGameInfo();
    startInfo.setMapName("empty10x10");
    player1 = createPlayerInfo(1, "iop");
    startInfo.setTeam1(List.of(player1));
    player2 = createPlayerInfo(2, "iop");
    startInfo.setTeam2(List.of(player2));
    game = new GridGame(randomProxy);
    game.initGame(startInfo);
  }

  @AfterEach
  public void cleanup() {
    game = null;
    randomProxy = null;
    player1 = null;
    player2 = null;
  }

  @Test
  public void concentration() {
    // given
    String spellName = "Concentration";
    PositionComponent position1 = new PositionComponent(0, 0);
    PositionComponent position2 = new PositionComponent(1, 0);

    int character1 = getCharacter(player1.getLogin());
    int character2 = getCharacter(player2.getLogin());
    int spell = getSpell(character1, spellName);

    set(character1, position1);
    set(character2, position2);

    int health2 = get(character2, HealthPointsComponent.class).getHealth();
    DamageComponent damageComponent = get(spell, DamageComponent.class);
    int damage = damageComponent.getMinDmg();
    Mockito.when(
            randomProxy.nextInt(
                Mockito.eq(damageComponent.getMinDmg()), Mockito.eq(damageComponent.getMaxDmg())))
        .thenReturn(damage);

    // when
    applyAction(
        new CastSpellAction(
            position2.getX(), position2.getY(), Integer.toString(character1), spell));

    // then
    assertEquals(health2 - damage, get(character2, HealthPointsComponent.class).getHealth());
  }

  @Test
  public void iopsWrath() {
    // given
    String spellName = "Iop's Wrath";
    PositionComponent position1 = new PositionComponent(0, 0);
    PositionComponent position2 = new PositionComponent(1, 0);

    int character1 = getCharacter(player1.getLogin());
    int character2 = getCharacter(player2.getLogin());
    int spell = getSpell(character1, spellName);

    set(character1, position1);
    set(character2, position2);

    int health2 = get(character2, HealthPointsComponent.class).getHealth();
    DamageComponent damageComponent = get(spell, DamageComponent.class);
    DamageBuffComponent buffComponent = get(spell, DamageBuffComponent.class);
    int cooldown = get(spell, CooldownComponent.class).getCooldown();
    int unbuffedDamage = damageComponent.getMinDmg();
    Mockito.when(
            randomProxy.nextInt(
                Mockito.eq(damageComponent.getMinDmg()), Mockito.eq(damageComponent.getMaxDmg())))
        .thenReturn(unbuffedDamage);
    int buffedDamage = damageComponent.getMinDmg() + buffComponent.getBuffAmount();
    Mockito.when(
            randomProxy.nextInt(
                Mockito.eq(damageComponent.getMinDmg() + buffComponent.getBuffAmount()),
                Mockito.eq(damageComponent.getMaxDmg() + buffComponent.getBuffAmount())))
        .thenReturn(buffedDamage);

    // when
    applyAction(
        new CastSpellAction(
            position2.getX(), position2.getY(), Integer.toString(character1), spell));
    for (int i = 0; i < cooldown; i++) {
      applyAction(new SkipRoundAction(Integer.toString(character1)));
      applyAction(new SkipRoundAction(Integer.toString(character2)));
    }
    applyAction(
        new CastSpellAction(
            position2.getX(), position2.getY(), Integer.toString(character1), spell));

    // then
    assertEquals(
        health2 - unbuffedDamage - buffedDamage,
        get(character2, HealthPointsComponent.class).getHealth());
  }

  @Test
  public void swordOfIop() {
    // given
    String spellName = "Sword of Iop";
    PositionComponent position1 = new PositionComponent(0, 0);
    PositionComponent position2 = new PositionComponent(4, 2);
    int targetX = 4;
    int targetY = 0;

    int character1 = getCharacter(player1.getLogin());
    int character2 = getCharacter(player2.getLogin());
    int spell = getSpell(character1, spellName);

    set(character1, position1);
    set(character2, position2);

    int health2 = get(character2, HealthPointsComponent.class).getHealth();
    DamageComponent damageComponent = get(spell, DamageComponent.class);
    int damage = damageComponent.getMinDmg();
    Mockito.when(
            randomProxy.nextInt(
                Mockito.eq(damageComponent.getMinDmg()), Mockito.eq(damageComponent.getMaxDmg())))
        .thenReturn(damage);

    // when
    applyAction(new CastSpellAction(targetX, targetY, Integer.toString(character1), spell));

    // then
    assertEquals(health2 - damage, get(character2, HealthPointsComponent.class).getHealth());
  }

  @Test
  public void outpouring() {
    // given
    String spellName = "Outpouring";
    PositionComponent position1 = new PositionComponent(0, 0);
    PositionComponent position2 = new PositionComponent(5, 0);

    int character1 = getCharacter(player1.getLogin());
    int character2 = getCharacter(player2.getLogin());
    int spell = getSpell(character1, spellName);

    set(character1, position1);
    set(character2, position2);

    // when
    applyAction(
        new CastSpellAction(
            position2.getX(), position2.getY(), Integer.toString(character1), spell));

    // then
    assertEquals(new PositionComponent(4, 0), get(character1, PositionComponent.class));
  }

  @Test
  public void threat() {
    // given
    String spellName = "Threat";
    PositionComponent position1 = new PositionComponent(0, 0);
    PositionComponent position2 = new PositionComponent(1, 2);

    int character1 = getCharacter(player1.getLogin());
    int character2 = getCharacter(player2.getLogin());
    int spell = getSpell(character1, spellName);

    set(character1, position1);
    set(character2, position2);

    // when
    applyAction(
        new CastSpellAction(
            position2.getX(), position2.getY(), Integer.toString(character1), spell));

    // then
    assertEquals(new PositionComponent(1, 0), get(character2, PositionComponent.class));
  }

  @Test
  public void blow() {
    // given
    String spellName = "Blow";
    PositionComponent position1 = new PositionComponent(0, 0);
    PositionComponent position2 = new PositionComponent(3, 0);

    int character1 = getCharacter(player1.getLogin());
    int character2 = getCharacter(player2.getLogin());
    int spell = getSpell(character1, spellName);

    set(character1, position1);
    set(character2, position2);

    // when
    applyAction(new CastSpellAction(4, 0, Integer.toString(character1), spell));

    // then
    assertEquals(new PositionComponent(2, 0), get(character2, PositionComponent.class));
  }

  private void set(int entity, Component component) {
    game.getData().addComponent(entity, component);
  }

  private <T extends Component> T get(int entity, Class<T> type) {
    return game.getData().getComponent(entity, type);
  }

  private void applyAction(Action action) {
    game.registerAction(action);
    while (game.triggeredHandlersInQueue()) {
      game.triggerNextHandler();
    }
  }

  private int getCharacter(String playerName) {
    for (Integer entity : game.getData().list(PlayerComponent.class)) {
      NameComponent nameComponent = game.getData().getComponent(entity, NameComponent.class);
      if (nameComponent.getName().equals(playerName)) {
        return entity;
      }
    }
    throw new AssertionError("Player " + playerName + " does not have a character.");
  }

  private int getSpell(int character, String spellName) {
    for (int spell : game.getData().getComponent(character, SpellsComponent.class).getSpells()) {
      NameComponent nameComponent = game.getData().getComponent(spell, NameComponent.class);
      if (nameComponent.getName().equals(spellName)) {
        return spell;
      }
    }
    throw new AssertionError("#" + character + " does not have a spell named " + spellName + ".");
  }

  private PlayerInfo createPlayerInfo(long id, String character) {
    PlayerInfo player = new PlayerInfo();
    player.setCharacterName(character);
    player.setId(id);
    player.setLogin("login-" + Long.toUnsignedString(id));
    return player;
  }
}
