package com.destrostudios.grid.game;

import com.destrostudios.grid.entities.EntityWorld;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;

public class GameStateConverter {

    public static String marshal(EntityWorld world) throws JAXBException {
        GameState gameState = new GameState();
        gameState.setWorld(world.getWorld());
        JAXBContext context = JAXBContext.newInstance(GameState.class);
        Marshaller mar= context.createMarshaller();
        mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        StringWriter sw = new StringWriter();
        mar.marshal(gameState, sw);
        return sw.toString();
    }

    public static EntityWorld unmarshal(String worldString) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(GameState.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        unmarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        StringReader sr = new StringReader(worldString);
        GameState state = (GameState) unmarshaller.unmarshal(sr);
        return new EntityWorld(state.getWorld());
    }
}
