package com.destrostudios.grid.components.spells;

import com.destrostudios.grid.components.Component;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OnCooldownComponent implements Component {
    private int remainingRounds;
}
