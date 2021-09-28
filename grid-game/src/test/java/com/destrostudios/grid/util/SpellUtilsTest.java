package com.destrostudios.grid.util;

import com.destrostudios.grid.components.map.PositionComponent;
import com.destrostudios.grid.components.spells.range.AffectedAreaComponent;
import com.destrostudios.grid.components.spells.range.SpellAreaShape;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SpellUtilsTest {

  private static Set<PositionComponent> toSet(char[][] array) {
    Set<PositionComponent> result = new HashSet<>();
    for (int y = 0; y < array.length; y++) {
      for (int x = 0; x < array[y].length; x++) {
        if (array[y][x] == 'X') {
          result.add(new PositionComponent(x, y));
        }
      }
    }
    return result;
  }

  @Test
  public void singleAoE() {
    PositionComponent sourcePos = new PositionComponent(0, 0);
    PositionComponent targetPos = new PositionComponent(3, 2);
    AffectedAreaComponent affectedAreaComponent =
        new AffectedAreaComponent(SpellAreaShape.SINGLE, 0, 0);
    Set<PositionComponent> actual =
        SpellUtils.calculateAffectedPositions(sourcePos, targetPos, affectedAreaComponent, null);
    assertEquals(Set.of(targetPos), actual);
  }

  @ParameterizedTest(
      name = "{index} - {4}: Impact ({2}, {3}), source {0}, target {1} => result should be {5} ")
  @ArgumentsSource(RangeUtilsTestArgumentProvider.class)
  public void plusAoE(
      PositionComponent sourcePos,
      PositionComponent targetPos,
      int minImpact,
      int maxImpact,
      SpellAreaShape affectedArea,
      char[][] expectedResult) {
    AffectedAreaComponent affectedAreaComponent =
        new AffectedAreaComponent(affectedArea, minImpact, maxImpact);
    Set<PositionComponent> actual =
        SpellUtils.calculateAffectedPositions(sourcePos, targetPos, affectedAreaComponent, null);
    assertEquals(toSet(expectedResult), actual);
  }
}
