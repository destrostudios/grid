package com.destrostudios.grid.components.properties;

import com.destrostudios.grid.components.Component;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class MaxMovementPointsComponent implements Component {
    private int maxMovenemtPoints;
}
