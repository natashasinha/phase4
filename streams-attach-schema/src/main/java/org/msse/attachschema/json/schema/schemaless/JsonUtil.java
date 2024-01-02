package org.msse.attachschema.json.schema.schemaless;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.TimeZone;

public final class JsonUtil {

  private JsonUtil() {
  }

  public static ObjectMapper objectMapper() {
    return new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setTimeZone(TimeZone.getDefault())
            .registerModule(new JavaTimeModule());
  }

}
