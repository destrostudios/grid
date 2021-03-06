package com.destrostudios.grid.eventbus.update.spells;

import com.destrostudios.grid.eventbus.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateSpellsEvent implements Event {
    private final int entity;
}
