package com.destrostudios.grid.gamestate;

import com.destrostudios.grid.components.Component;
import com.destrostudios.grid.entities.EntityWorld;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GameStateConverter {

    public static String marshal(EntityWorld world) throws JAXBException {
        GameState gameState = new GameState();
        Map<Integer, ComponentsWrapper> componentsByEntity = world.getWorld().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> new ComponentsWrapper(e.getValue())));
        gameState.setWorld(componentsByEntity);
        JAXBContext context = JAXBContext.newInstance(GameState.class);
        Marshaller mar = context.createMarshaller();
        mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        StringWriter sw = new StringWriter();
        mar.marshal(gameState, sw);
        return sw.toString();
    }

    public static GameState unmarshal(String worldString) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(GameState.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        StringReader sr = new StringReader(worldString);
        GameState state = (GameState) unmarshaller.unmarshal(sr);
        return state;
    }
}
