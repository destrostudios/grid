package com.destrostudios.grid.bot;

import com.destrostudios.grid.components.Component;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class ManhattanDistanceScore implements Component {
    float[] distanceScores;
}
