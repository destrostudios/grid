package com.destrostudios.grid.util;

import com.destrostudios.grid.components.map.PositionComponent;
import com.destrostudios.grid.components.spells.range.AffectedAreaIndicator;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class RangeUtilsTestArgumentProvider  implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
        return Stream.of(
                Arguments.of(
                        new PositionComponent(0, 0), new PositionComponent(3, 2), 0, 2, AffectedAreaIndicator.PLUS,
                        new char[][]{
                                {' ', ' ', ' ', 'X', ' ', ' '},
                                {' ', ' ', ' ', 'X', ' ', ' '},
                                {' ', 'X', 'X', 'X', 'X', 'X'},
                                {' ', ' ', ' ', 'X', ' ', ' '},
                                {' ', ' ', ' ', 'X', ' ', ' '}
                        }
                ),
                Arguments.of(
                        new PositionComponent(0, 0), new PositionComponent(3, 2), 1, 2,AffectedAreaIndicator.PLUS,
                        new char[][]{
                                {' ', ' ', ' ', 'X', ' ', ' '},
                                {' ', ' ', ' ', 'X', ' ', ' '},
                                {' ', 'X', 'X', ' ', 'X', 'X'},
                                {' ', ' ', ' ', 'X', ' ', ' '},
                                {' ', ' ', ' ', 'X', ' ', ' '}
                        }
                ),
                Arguments.of(
                        new PositionComponent(0, 0), new PositionComponent(3, 2), 2, 2,AffectedAreaIndicator.PLUS,
                        new char[][]{
                                {' ', ' ', ' ', 'X', ' ', ' '},
                                {' ', ' ', ' ', ' ', ' ', ' '},
                                {' ', 'X', ' ', ' ', ' ', 'X'},
                                {' ', ' ', ' ', ' ', ' ', ' '},
                                {' ', ' ', ' ', 'X', ' ', ' '}
                        }
                ),
                Arguments.of(
                        new PositionComponent(0, 0), new PositionComponent(3, 2), 0, 2,AffectedAreaIndicator.DIAMOND,
                        new char[][]{
                                {' ', ' ', ' ', 'X', ' ', ' '},
                                {' ', ' ', 'X', 'X', 'X', ' '},
                                {' ', 'X', 'X', 'X', 'X', 'X'},
                                {' ', ' ', 'X', 'X', 'X', ' '},
                                {' ', ' ', ' ', 'X', ' ', ' '}
                        }
                ),
                Arguments.of(
                        new PositionComponent(0, 0), new PositionComponent(3, 2), 1, 2,AffectedAreaIndicator.DIAMOND,
                        new char[][]{
                                {' ', ' ', ' ', 'X', ' ', ' '},
                                {' ', ' ', 'X', 'X', 'X', ' '},
                                {' ', 'X', 'X', ' ', 'X', 'X'},
                                {' ', ' ', 'X', 'X', 'X', ' '},
                                {' ', ' ', ' ', 'X', ' ', ' '}
                        }
                ),
                Arguments.of(
                        new PositionComponent(0, 0), new PositionComponent(3, 2), 2, 2,AffectedAreaIndicator.DIAMOND,
                        new char[][]{
                                {' ', ' ', ' ', 'X', ' ', ' '},
                                {' ', ' ', 'X', ' ', 'X', ' '},
                                {' ', 'X', ' ', ' ', ' ', 'X'},
                                {' ', ' ', 'X', ' ', 'X', ' '},
                                {' ', ' ', ' ', 'X', ' ', ' '}
                        }
                ),
                Arguments.of(
                        new PositionComponent(0, 0), new PositionComponent(3, 2), 0, 2,AffectedAreaIndicator.SQUARE,
                        new char[][]{
                                {' ', 'X', 'X', 'X', 'X', 'X'},
                                {' ', 'X', 'X', 'X', 'X', 'X'},
                                {' ', 'X', 'X', 'X', 'X', 'X'},
                                {' ', 'X', 'X', 'X', 'X', 'X'},
                                {' ', 'X', 'X', 'X', 'X', 'X'}
                        }
                ),
                Arguments.of(
                        new PositionComponent(0, 0), new PositionComponent(3, 2), 1, 2,AffectedAreaIndicator.SQUARE,
                        new char[][]{
                                {' ', 'X', 'X', 'X', 'X', 'X'},
                                {' ', 'X', 'X', 'X', 'X', 'X'},
                                {' ', 'X', 'X', ' ', 'X', 'X'},
                                {' ', 'X', 'X', 'X', 'X', 'X'},
                                {' ', 'X', 'X', 'X', 'X', 'X'}
                        }
                ),
                Arguments.of(
                        new PositionComponent(0, 0), new PositionComponent(3, 2), 2, 2,AffectedAreaIndicator.SQUARE,
                        new char[][]{
                                {' ', 'X', 'X', 'X', 'X', 'X'},
                                {' ', 'X', ' ', ' ', ' ', 'X'},
                                {' ', 'X', ' ', ' ', ' ', 'X'},
                                {' ', 'X', ' ', ' ', ' ', 'X'},
                                {' ', 'X', 'X', 'X', 'X', 'X'}
                        }
                ),
                Arguments.of(
                        new PositionComponent(0, 0), new PositionComponent(3, 2), 0, 2,AffectedAreaIndicator.LINE,
                        new char[][]{
                                {' ', ' ', ' ', ' ', ' ', ' '},
                                {' ', ' ', ' ', ' ', ' ', ' '},
                                {' ', ' ', ' ', 'X', 'X', 'X'},
                                {' ', ' ', ' ', ' ', ' ', ' '},
                                {' ', ' ', ' ', ' ', ' ', ' '}
                        }
                ),
                Arguments.of(
                        new PositionComponent(0, 0), new PositionComponent(3, 2), 1, 2,AffectedAreaIndicator.LINE,
                        new char[][]{
                                {' ', ' ', ' ', ' ', ' ', ' '},
                                {' ', ' ', ' ', ' ', ' ', ' '},
                                {' ', ' ', ' ', ' ', 'X', 'X'},
                                {' ', ' ', ' ', ' ', ' ', ' '},
                                {' ', ' ', ' ', ' ', ' ', ' '}
                        }
                ),
                Arguments.of(
                        new PositionComponent(0, 0), new PositionComponent(3, 2), 2, 2,AffectedAreaIndicator.LINE,
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
