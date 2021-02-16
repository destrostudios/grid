package com.destrostudios.grid.components.properties;

import com.destrostudios.grid.components.Component;
import com.google.common.collect.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BuffsComponent implements Component {
    private List<Integer> buffEntities = new ArrayList<>(); // points to buff entity
}
