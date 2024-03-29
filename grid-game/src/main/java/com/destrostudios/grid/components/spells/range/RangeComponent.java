package com.destrostudios.grid.components.spells.range;

import com.destrostudios.grid.components.Component;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class RangeComponent implements Component {
  CastAreaShape castAreaShape;
  int minRange;
  int maxRange;
}
