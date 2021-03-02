package com.destrostudios.grid.components.properties;

import com.destrostudios.grid.components.Component;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpellsComponent implements Component {
    private List<Integer> spells;
}
