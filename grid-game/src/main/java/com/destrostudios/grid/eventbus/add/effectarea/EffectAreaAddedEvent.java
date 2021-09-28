package com.destrostudios.grid.eventbus.add.effectarea;

import com.destrostudios.grid.eventbus.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class EffectAreaAddedEvent implements Event {
  private final int spellEntity;
  List<Integer> affectedEntities;
}
