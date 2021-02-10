package com.destrostudios.grid.gamestate;

import com.destrostudios.grid.entities.EntityMap;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;

@XmlRootElement(name = "GameState")
@XmlAccessorType(XmlAccessType.FIELD)
public class GameState {

    @Getter
    @Setter
    @XmlElement(name = "Components")
    private Map<Integer, ComponentsWrapper> world;


    @Getter
    @Setter
    @XmlElement(name = "Map")
    private int[][] map;


}
