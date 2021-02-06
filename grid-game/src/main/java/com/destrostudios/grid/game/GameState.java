package com.destrostudios.grid.game;

import com.destrostudios.grid.components.Component;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Map;

@XmlRootElement(name = "GameState")
public class GameState {

    @Getter
    @Setter
    @XmlAttribute
    private Map<Integer, List<Component>> world;

}
