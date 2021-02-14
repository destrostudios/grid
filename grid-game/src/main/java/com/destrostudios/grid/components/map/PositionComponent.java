package com.destrostudios.grid.components.map;

import com.destrostudios.grid.components.Component;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class PositionComponent implements Component {
    private int x;
    private int y;

}
