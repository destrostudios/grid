package com.destrostudios.grid.eventbus.action.damagetaken;

import com.destrostudios.grid.eventbus.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DamageTakenEvent implements Event {
    private final int damage;
    private final int targetEntity;
}
