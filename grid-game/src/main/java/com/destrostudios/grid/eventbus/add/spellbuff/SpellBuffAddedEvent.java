package com.destrostudios.grid.eventbus.add.spellbuff;

import com.destrostudios.grid.eventbus.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SpellBuffAddedEvent implements Event {
    private final int spellEntity;
}
