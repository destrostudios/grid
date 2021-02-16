package com.destrostudios.grid.eventbus.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BuffAddedEvent implements Event{
    private final int targetEntity;
    private final int spellEntity;
}
