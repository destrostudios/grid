package com.destrostudios.grid.components.spells.limitations;

import com.destrostudios.grid.components.Component;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class CostComponent implements Component {
  int apCost;
  int mpCost;
  int hpCost;
}
