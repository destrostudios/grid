package com.destrostudios.grid.game;

import com.destrostudios.grid.components.Component;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@XmlRootElement(name = "GameState")
public class GameState {

    @Getter
    @Setter
    @XmlAttribute
    private Map<Integer, List<Component>> world;

}
