package com.destrostudios.grid.components.properties.resistence;

import com.destrostudios.grid.components.Component;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class AttackPointResistenceComponent implements Component {
    private int resitanceValue;
}
