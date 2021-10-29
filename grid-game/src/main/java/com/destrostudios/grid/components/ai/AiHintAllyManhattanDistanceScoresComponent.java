package com.destrostudios.grid.components.ai;

import com.destrostudios.grid.components.Component;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class AiHintAllyManhattanDistanceScoresComponent implements Component {
    float[] distanceScores;
}
