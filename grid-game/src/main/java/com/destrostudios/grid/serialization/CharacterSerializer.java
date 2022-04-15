package com.destrostudios.grid.serialization;

import com.destrostudios.grid.components.Component;
import com.destrostudios.grid.serialization.container.CharacterContainer;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CharacterSerializer extends StdSerializer<CharacterContainer> {

  public CharacterSerializer() {
    super(CharacterContainer.class);
  }

  protected CharacterSerializer(Class<CharacterContainer> t) {
    super(t);
  }

  @Override
  public void serialize(
      CharacterContainer container, JsonGenerator jgen, SerializerProvider provider)
      throws IOException {
    Map<Integer, List<Component>> charComponents = new HashMap<>();
    Map<Integer, List<Component>> spellComponents = new HashMap<>();
    jgen.writeStartObject();
    jgen.writeObjectField("character", charComponents);
    jgen.writeObjectField("spells", spellComponents);

    jgen.writeEndObject();
  }
}
