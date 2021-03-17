package com.destrostudios.grid.util;

import com.destrostudios.grid.components.map.PositionComponent;
import com.destrostudios.grid.components.spells.range.SpellAreaShape;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

public class RangeUtilsTestArgumentProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
        return Stream.of(
                Arguments.of(
                        new PositionComponent(0, 0), new PositionComponent(3, 2), 0, 2, SpellAreaShape.PLUS,
                        new char[][]{
                                {' ', ' ', ' ', 'X', ' ', ' '},
                                {' ', ' ', ' ', 'X', ' ', ' '},
                                {' ', 'X', 'X', 'X', 'X', 'X'},
                                {' ', ' ', ' ', 'X', ' ', ' '},
                                {' ', ' ', ' ', 'X', ' ', ' '}
                        }
                ),
                Arguments.of(
                        new PositionComponent(0, 0), new PositionComponent(3, 2), 1, 2, SpellAreaShape.PLUS,
                        new char[][]{
                                {' ', ' ', ' ', 'X', ' ', ' '},
                                {' ', ' ', ' ', 'X', ' ', ' '},
                                {' ', 'X', 'X', ' ', 'X', 'X'},
                                {' ', ' ', ' ', 'X', ' ', ' '},
                                {' ', ' ', ' ', 'X', ' ', ' '}
                        }
                ),
                Arguments.of(
                        new PositionComponent(0, 0), new PositionComponent(3, 2), 2, 2, SpellAreaShape.PLUS,
                        new char[][]{
                                {' ', ' ', ' ', 'X', ' ', ' '},
                                {' ', ' ', ' ', ' ', ' ', ' '},
                                {' ', 'X', ' ', ' ', ' ', 'X'},
                                {' ', ' ', ' ', ' ', ' ', ' '},
                                {' ', ' ', ' ', 'X', ' ', ' '}
                        }
                ),
                Arguments.of(
                        new PositionComponent(0, 0), new PositionComponent(3, 2), 0, 2, SpellAreaShape.DIAMOND,
                        new char[][]{
                                {' ', ' ', ' ', 'X', ' ', ' '},
                                {' ', ' ', 'X', 'X', 'X', ' '},
                                {' ', 'X', 'X', 'X', 'X', 'X'},
                                {' ', ' ', 'X', 'X', 'X', ' '},
                                {' ', ' ', ' ', 'X', ' ', ' '}
                        }
                ),
                Arguments.of(
                        new PositionComponent(0, 0), new PositionComponent(3, 2), 1, 2, SpellAreaShape.DIAMOND,
                        new char[][]{
                                {' ', ' ', ' ', 'X', ' ', ' '},
                                {' ', ' ', 'X', 'X', 'X', ' '},
                                {' ', 'X', 'X', ' ', 'X', 'X'},
                                {' ', ' ', 'X', 'X', 'X', ' '},
                                {' ', ' ', ' ', 'X', ' ', ' '}
                        }
                ),
                Arguments.of(
                        new PositionComponent(0, 0), new PositionComponent(3, 2), 2, 2, SpellAreaShape.DIAMOND,
                        new char[][]{
                                {' ', ' ', ' ', 'X', ' ', ' '},
                                {' ', ' ', 'X', ' ', 'X', ' '},
                                {' ', 'X', ' ', ' ', ' ', 'X'},
                                {' ', ' ', 'X', ' ', 'X', ' '},
                                {' ', ' ', ' ', 'X', ' ', ' '}
                        }
                ),
                Arguments.of(
                        new PositionComponent(0, 0), new PositionComponent(3, 2), 0, 2, SpellAreaShape.SQUARE,
                        new char[][]{
                                {' ', 'X', 'X', 'X', 'X', 'X'},
                                {' ', 'X', 'X', 'X', 'X', 'X'},
                                {' ', 'X', 'X', 'X', 'X', 'X'},
                                {' ', 'X', 'X', 'X', 'X', 'X'},
                                {' ', 'X', 'X', 'X', 'X', 'X'}
                        }
                ),
                Arguments.of(
                        new PositionComponent(0, 0), new PositionComponent(3, 2), 1, 2, SpellAreaShape.SQUARE,
                        new char[][]{
                                {' ', 'X', 'X', 'X', 'X', 'X'},
                                {' ', 'X', 'X', 'X', 'X', 'X'},
                                {' ', 'X', 'X', ' ', 'X', 'X'},
                                {' ', 'X', 'X', 'X', 'X', 'X'},
                                {' ', 'X', 'X', 'X', 'X', 'X'}
                        }
                ),
                Arguments.of(
                        new PositionComponent(0, 0), new PositionComponent(3, 2), 2, 2, SpellAreaShape.SQUARE,
                        new char[][]{
                                {' ', 'X', 'X', 'X', 'X', 'X'},
                                {' ', 'X', ' ', ' ', ' ', 'X'},
                                {' ', 'X', ' ', ' ', ' ', 'X'},
                                {' ', 'X', ' ', ' ', ' ', 'X'},
                                {' ', 'X', 'X', 'X', 'X', 'X'}
                        }
                ),
                Arguments.of(
                        new PositionComponent(0, 0), new PositionComponent(3, 2), 0, 2, SpellAreaShape.LINE,
                        new char[][]{
                                {' ', ' ', ' ', ' ', ' ', ' '},
                                {' ', ' ', ' ', ' ', ' ', ' '},
                                {' ', ' ', ' ', 'X', 'X', 'X'},
                                {' ', ' ', ' ', ' ', ' ', ' '},
                                {' ', ' ', ' ', ' ', ' ', ' '}
                        }
                ),
                Arguments.of(
                        new PositionComponent(0, 0), new PositionComponent(3, 2), 1, 2, SpellAreaShape.LINE,
                        new char[][]{
                                {' ', ' ', ' ', ' ', ' ', ' '},
                                {' ', ' ', ' ', ' ', ' ', ' '},
                                {' ', ' ', ' ', ' ', 'X', 'X'},
                                {' ', ' ', ' ', ' ', ' ', ' '},
                                {' ', ' ', ' ', ' ', ' ', ' '}
                        }
                ),
                Arguments.of(
                        new PositionComponent(0, 0), new PositionComponent(3, 2), 2, 2, SpellAreaShape.LINE,
                        new char[][]{
                                {' ', ' ', ' ', ' ', ' ', ' '},
                                {' ', ' ', ' ', ' ', ' ', ' '},
                                {' ', ' ', ' ', ' ', ' ', 'X'},
                                {' ', ' ', ' ', ' ', ' ', ' '},
                                {' ', ' ', ' ', ' ', ' ', ' '}
                        }
                )
        );
    }
}
