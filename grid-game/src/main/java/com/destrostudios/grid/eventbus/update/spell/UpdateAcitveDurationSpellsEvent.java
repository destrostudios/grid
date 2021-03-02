package com.destrostudios.grid.eventbus.update.spell;

import com.destrostudios.grid.eventbus.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateAcitveDurationSpellsEvent implements Event {
    private final int targetEntity;
}
