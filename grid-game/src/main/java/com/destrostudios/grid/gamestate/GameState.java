package com.destrostudios.grid.gamestate;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;
import java.util.Map;

@XmlRootElement(name = "GameState")
@XmlAccessorType(XmlAccessType.FIELD)
public class GameState {

    @Getter
    @Setter
    @XmlElement(name = "Components")
    private Map<Integer, ComponentsWrapper> world;

}
