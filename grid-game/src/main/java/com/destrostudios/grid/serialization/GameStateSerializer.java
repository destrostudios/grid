package com.destrostudios.grid.serialization;

import com.destrostudios.grid.entities.EntityWorld;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;

public class GameStateSerializer {

    public static GameState readGamestate(String gameState) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter();
        return mapper.readValue(gameState, GameState.class);
    }

    public static String getGamestateString(EntityWorld world) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter();
        GameState gameState = new GameState();
        Map<Integer, ComponentsWrapper> componentsByEntity = world.getWorld().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> new ComponentsWrapper(e.getValue())));
        gameState.setWorld(componentsByEntity);
        return mapper.writeValueAsString(gameState);
    }

}
