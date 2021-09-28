package com.destrostudios.grid.components.properties;

import com.destrostudios.grid.components.Component;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class StatsPerRoundComponent implements Component {
  private List<Integer> statsPerRoundEntites; // points to buff entity
}
