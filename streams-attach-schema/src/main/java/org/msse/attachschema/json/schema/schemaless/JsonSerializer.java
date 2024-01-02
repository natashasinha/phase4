package org.msse.attachschema.json.schema.schemaless;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;
import java.util.TimeZone;

public class JsonSerializer implements Serializer<JsonNode> {

    private static final ObjectMapper OBJECT_MAPPER = JsonUtil.objectMapper();

    @SuppressWarnings("unused")
    public JsonSerializer() {
        // needed by kafka
    }

    @Override
    public byte[] serialize(String topic, JsonNode data) {

        if (data == null) {
            return null; //NOSONAR - sonar is wrong, returning empty array would be wrong - I hate sonar.
        }

        try {
            return OBJECT_MAPPER.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new SerializationException("Error serializing JSON message", e);
        }
    }

}
