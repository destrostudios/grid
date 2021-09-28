package com.destrostudios.grid.eventbus.action.spellcasted;

import com.destrostudios.grid.eventbus.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SpellCastedEvent implements Event {
  private final int spell;
  private final int playerEntity;
  private final int x;
  private final int y;
}
