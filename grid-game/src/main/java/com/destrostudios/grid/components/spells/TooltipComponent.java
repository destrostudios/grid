package com.destrostudios.grid.components.spells;

import com.destrostudios.grid.components.Component;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TooltipComponent implements Component {
    private String tooltip;
}
