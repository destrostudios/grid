package com.destrostudios.grid.game.gamestate;

import com.destrostudios.grid.components.Component;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.List;

@Getter
@Setter
@XmlRootElement(name = "Components")
@XmlAccessorType(XmlAccessType.FIELD)
@AllArgsConstructor
@NoArgsConstructor
public class ComponentsWrapper {

    private List<Component> components;
}
