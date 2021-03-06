package com.destrostudios.grid.eventbus.action.displace;

import com.destrostudios.grid.eventbus.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DisplacementEvent implements Event {
    private final int entityToDisplace;
    private final int displacementAmount;
    private final int xDisplacementSource;
    private final int yDisplacementSource;
}
