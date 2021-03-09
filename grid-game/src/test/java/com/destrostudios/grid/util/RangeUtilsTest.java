package com.destrostudios.grid.util;

import com.destrostudios.grid.components.map.PositionComponent;
import com.destrostudios.grid.components.spells.range.AffectedAreaComponent;
import com.destrostudios.grid.components.spells.range.AffectedAreaIndicator;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RangeUtilsTest {

    @Test
    public void singleAoE() {
        PositionComponent sourcePos = new PositionComponent(0, 0);
        PositionComponent targetPos = new PositionComponent(3, 2);
        AffectedAreaComponent affectedAreaComponent = new AffectedAreaComponent(AffectedAreaIndicator.SINGLE, 0, 0);
        Set<PositionComponent> actual = RangeUtils.calculateAffectedPositions(sourcePos, targetPos, affectedAreaComponent);
        assertEquals(Set.of(targetPos), actual);
    }

    //    @Test
    public void plusAoE() {
        PositionComponent sourcePos = new PositionComponent(0, 0);
        PositionComponent targetPos = new PositionComponent(3, 2);
        AffectedAreaComponent affectedAreaComponent = new AffectedAreaComponent(AffectedAreaIndicator.PLUS, 1, 2);
        Set<PositionComponent> actual = RangeUtils.calculateAffectedPositions(sourcePos, targetPos, affectedAreaComponent);

        assertEquals(toSet(
                new char[][]{
                        {' ', ' ', ' ', 'X', ' ', ' '},
                        {' ', ' ', ' ', 'X', ' ', ' '},
                        {' ', 'X', 'X', ' ', 'X', 'X'},
                        {' ', ' ', ' ', 'X', ' ', ' '},
                        {' ', ' ', ' ', 'X', ' ', ' '}
                }
        ), actual);
    }

    //    @Test
    public void diamondAoE() {
        PositionComponent sourcePos = new PositionComponent(0, 0);
        PositionComponent targetPos = new PositionComponent(3, 2);
        AffectedAreaComponent affectedAreaComponent = new AffectedAreaComponent(AffectedAreaIndicator.DIAMOND, 2, 2);
        Set<PositionComponent> actual = RangeUtils.calculateAffectedPositions(sourcePos, targetPos, affectedAreaComponent);

        assertEquals(toSet(
                new char[][]{
                        {' ', ' ', ' ', 'X', ' ', ' '},
                        {' ', ' ', 'X', ' ', 'X', ' '},
                        {' ', 'X', ' ', ' ', ' ', 'X'},
                        {' ', ' ', 'X', ' ', 'X', ' '},
                        {' ', ' ', ' ', 'X', ' ', ' '}
                }
        ), actual);
    }

    //    @Test
    public void squareAoE() {
        PositionComponent sourcePos = new PositionComponent(0, 0);
        PositionComponent targetPos = new PositionComponent(3, 2);
        AffectedAreaComponent affectedAreaComponent = new AffectedAreaComponent(AffectedAreaIndicator.SQUARE, 2, 2);
        Set<PositionComponent> actual = RangeUtils.calculateAffectedPositions(sourcePos, targetPos, affectedAreaComponent);

        assertEquals(toSet(
                new char[][]{
                        {' ', 'X', 'X', 'X', 'X', 'X'},
                        {' ', 'X', ' ', ' ', ' ', 'X'},
                        {' ', 'X', ' ', ' ', ' ', 'X'},
                        {' ', 'X', ' ', ' ', ' ', 'X'},
                        {' ', 'X', 'X', 'X', 'X', 'X'}
                }
        ), actual);
    }

    //    @Test
    public void lineAoE() {
        PositionComponent sourcePos = new PositionComponent(0, 0);
        PositionComponent targetPos = new PositionComponent(3, 2);
        AffectedAreaComponent affectedAreaComponent = new AffectedAreaComponent(AffectedAreaIndicator.LINE, 1, 2);
        Set<PositionComponent> actual = RangeUtils.calculateAffectedPositions(sourcePos, targetPos, affectedAreaComponent);

        assertEquals(toSet(
                new char[][]{
                        {' ', ' ', ' ', ' ', ' ', ' '},
                        {' ', ' ', ' ', ' ', ' ', ' '},
                        {' ', ' ', ' ', ' ', 'X', 'X'},
                        {' ', ' ', ' ', ' ', ' ', ' '},
                        {' ', ' ', ' ', ' ', ' ', ' '}
                }
        ), actual);
    }

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
}
