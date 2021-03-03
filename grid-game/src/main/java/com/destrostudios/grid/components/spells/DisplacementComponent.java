package com.destrostudios.grid.components.spells;

import com.destrostudios.grid.components.Component;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DisplacementComponent implements Component {
    private int displacement;
}
