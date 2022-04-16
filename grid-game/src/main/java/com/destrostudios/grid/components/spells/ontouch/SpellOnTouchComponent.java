package com.destrostudios.grid.components.spells.ontouch;

import com.destrostudios.grid.components.Component;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class SpellOnTouchComponent implements Component {
  @Setter private int spell;

  public int getSpell() {
    return spell;
  }
}
