package com.destrostudios.grid.serialization.container;

import com.destrostudios.grid.components.Component;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;
import java.util.Map;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "SeriazableComponent")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CharacterContainer.class, name = "character"),
        @JsonSubTypes.Type(value = MapContainer.class, name = "map")
})
public interface ComponentsContainer {
    Map<Integer, List<Component>> getComponents();
}
