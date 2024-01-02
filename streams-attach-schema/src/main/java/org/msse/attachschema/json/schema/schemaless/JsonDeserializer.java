package org.msse.attachschema.json.schema.schemaless;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;
import java.util.TimeZone;

public class JsonDeserializer implements Deserializer<JsonNode> {

    private static final ObjectMapper OBJECT_MAPPER = JsonUtil.objectMapper();

    @SuppressWarnings("unused")
    public JsonDeserializer() {
        // needed by kafka
    }

    @Override
    public JsonNode deserialize(String topic, byte[] bytes) {

        if (bytes == null) {
            return null;
        }

        try {
            return OBJECT_MAPPER.readTree(bytes);
        } catch (final IOException e) {
            throw new SerializationException(e);
        }
    }

}