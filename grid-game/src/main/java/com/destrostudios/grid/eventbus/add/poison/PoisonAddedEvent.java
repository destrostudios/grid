package com.destrostudios.grid.eventbus.add.poison;

import com.destrostudios.grid.eventbus.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PoisonAddedEvent implements Event {
    private final int sourceEntity;
    private final int targetEntity;
    private final int spellEntity;
}
