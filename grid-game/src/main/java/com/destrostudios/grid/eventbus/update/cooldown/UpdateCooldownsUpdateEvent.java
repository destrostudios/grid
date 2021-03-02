package com.destrostudios.grid.eventbus.update.cooldown;

import com.destrostudios.grid.eventbus.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateCooldownsUpdateEvent implements Event {
    private final int entity;
}
