package com.destrostudios.grid.eventbus.add.playerbuff;

import com.destrostudios.grid.eventbus.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PlayerBuffAddedEvent implements Event {
    private final int targetEntity;
    private final int spellEntity;
}
