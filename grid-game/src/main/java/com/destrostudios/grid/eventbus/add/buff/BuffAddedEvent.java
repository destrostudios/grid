package com.destrostudios.grid.eventbus.add.buff;

import com.destrostudios.grid.eventbus.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BuffAddedEvent implements Event {
    private final int targetEntity;
    private final int spellEntity;
}
