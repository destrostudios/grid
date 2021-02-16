package com.destrostudios.grid.eventbus.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SpellCastedEvent implements Event {
    private final int spell;
    private final int playerEntity;
    private final int targetEntity;
}
