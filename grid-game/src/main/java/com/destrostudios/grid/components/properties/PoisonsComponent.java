package com.destrostudios.grid.components.properties;

import com.destrostudios.grid.components.Component;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PoisonsComponent implements Component {
    private List<Integer> poisonsEntities = new ArrayList<>(); // points to buff entity
}
