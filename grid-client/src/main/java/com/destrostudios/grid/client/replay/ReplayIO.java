package com.destrostudios.grid.client.replay;

import com.destrostudios.grid.actions.Action;
import com.destrostudios.grid.actions.CastSpellAction;
import com.destrostudios.grid.actions.PositionUpdateAction;
import com.destrostudios.grid.actions.SkipRoundAction;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.IOException;
import java.nio.file.Path;

public class ReplayIO {

    public static void write(GameReplay replay, Path out) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(out.toFile(), replay);
    }

    public static GameReplay read(Path in) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new SimpleModule().addDeserializer(Action.class, new JsonDeserializer<>() {
            @Override
            public Action deserialize(JsonParser parser, DeserializationContext context) throws IOException {
                JsonNode node = context.readTree(parser);
                if (node.has("spell")) {
                    return mapper.treeToValue(node, CastSpellAction.class);
                }
                if (node.has("newX")) {
                    return mapper.treeToValue(node, PositionUpdateAction.class);
                }
                if (node.has("playerIdentifier")) {
                    return mapper.treeToValue(node, SkipRoundAction.class);
                }
                throw new AssertionError();
            }
        }));
        return mapper.readValue(in.toFile(), GameReplay.class);
    }
}
