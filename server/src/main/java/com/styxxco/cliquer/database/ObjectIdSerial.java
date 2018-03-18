package com.styxxco.cliquer.database;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import org.bson.types.ObjectId;

import java.io.IOException;

public class ObjectIdSerial {

    public static class ObjectIdJsonSerializer extends JsonSerializer<ObjectId> {
        @Override
        public void serialize(ObjectId o, JsonGenerator j, SerializerProvider s) throws IOException, JsonProcessingException {
            if(o == null) {
                j.writeNull();
            } else {
                j.writeString(o.toHexString());
            }
        }
    }

    public class ObjectIdDeserializer extends JsonDeserializer<ObjectId> {

        @Override
        public ObjectId deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            JsonNode oid = ((JsonNode) p.readValueAsTree()).get("$oid");
            return new ObjectId(oid.asText());
        }

    }
}
