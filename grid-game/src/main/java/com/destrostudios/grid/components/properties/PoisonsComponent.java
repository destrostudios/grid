package com.destrostudios.grid.components.properties;

import com.destrostudios.grid.components.Component;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class PoisonsComponent implements Component {
    private List<Integer> poisonsEntities; // points to buff entity
}
