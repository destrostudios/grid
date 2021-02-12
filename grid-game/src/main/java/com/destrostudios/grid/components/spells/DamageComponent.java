package com.destrostudios.grid.components.spells;

import com.destrostudios.grid.components.Component;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DamageComponent implements Component {
    private final int damage;
}
