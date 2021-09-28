package com.destrostudios.grid.components.spells.buffs;

import com.destrostudios.grid.components.Component;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class BuffComponent implements Component {
  private int buffAmount;
  private int buffDuration;
  private BuffType buffType;
}
