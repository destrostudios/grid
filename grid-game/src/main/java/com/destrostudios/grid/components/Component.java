package com.destrostudios.grid.components;

import com.destrostudios.grid.game.gamestate.ComponentAdapter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlJavaTypeAdapter(ComponentAdapter.class)
public interface Component {

    default String toMarshalString(){
        return getClass().getSimpleName();
    }
}
