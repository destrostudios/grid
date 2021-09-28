package com.destrostudios.grid.components.spells.movements;

import com.destrostudios.grid.components.Component;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class PushComponent implements Component {
  private int displacement;
  private boolean useTargetAsOrigin;
}
