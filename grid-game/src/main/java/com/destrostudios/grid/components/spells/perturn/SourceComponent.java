package com.destrostudios.grid.components.spells.perturn;

import com.destrostudios.grid.components.Component;
import lombok.*;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class SourceComponent  implements Component {
    int sourceEntity;
}
