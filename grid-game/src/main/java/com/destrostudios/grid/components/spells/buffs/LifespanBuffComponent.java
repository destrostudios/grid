package com.destrostudios.grid.components.spells.buffs;

import com.destrostudios.grid.components.Component;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LifespanBuffComponent implements Component, BuffComponent {
  int lifespanBuff;
  private BuffType buffType;
}
