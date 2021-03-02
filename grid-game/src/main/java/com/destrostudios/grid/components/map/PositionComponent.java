package com.destrostudios.grid.components.map;

import com.destrostudios.grid.components.Component;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PositionComponent implements Component {
    private int x;
    private int y;

}
