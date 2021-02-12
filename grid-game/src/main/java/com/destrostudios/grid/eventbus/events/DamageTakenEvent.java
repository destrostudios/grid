package com.destrostudios.grid.eventbus.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DamageTakenEvent implements Event {
    private final int damage;
    private final int targetEntity;
}
