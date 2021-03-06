package com.destrostudios.grid.eventbus.update.playerenchantments;

import com.destrostudios.grid.eventbus.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdatePlayerEnchantmentsEvent implements Event {
    private final int targetEntity;
}
