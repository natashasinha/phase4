package org.msse.attachschema.streams;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.confluent.kafka.schemaregistry.json.JsonSchema;
import io.confluent.kafka.schemaregistry.json.JsonSchemaUtils;
import io.confluent.kafka.streams.serdes.json.KafkaJsonSchemaSerde;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.processor.api.FixedKeyProcessor;
import org.apache.kafka.streams.processor.api.FixedKeyProcessorContext;
import org.apache.kafka.streams.processor.api.FixedKeyProcessorSupplier;
import org.apache.kafka.streams.processor.api.FixedKeyRecord;
import org.msse.attachschema.config.KafkaUtil;
import org.msse.attachschema.config.PropertiesUtil;
import org.msse.attachschema.json.schema.schemaless.JsonSerde;
import org.msse.attachschema.json.schema.schemaless.JsonUtil;
import org.msse.attachschema.util.Inflector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class Streams {

  private static final Logger log = LoggerFactory.getLogger(Streams.class);

  private static final String CONFIG_PREFIX = "KAFKA_STREAMS_";
  private static final String CONNECTION_SECRETS = "/mnt/secrets/connection.properties";
  public static final String ADDRESS = "address";
  public static final String CUSTOMER = "customer";
  public static final String EMAIL = "email";
  public static final String EVENT = "event";
  public static final String PHONE = "phone";
  public static final String STREAM = "stream";
  public static final String TICKET = "ticket";
  public static final String VENUE = "venue";


  // Kafka Streams does not provide the ability to leverage record metadata in many DSL functions, such as mapValues().
  // This application gives a technique of how to use one function to pull the metadata into the model, so it can be
  // used in those other operations.
  private static final String TYPE_FIELD = "_type";
  // this is the field used when JsonSchemaUtils' envelope method is used.
  private static final String PAYLOAD_FIELD = "payload";

  private final Options options;

  private final Map<String, JsonSchema> schemas = new HashMap<>();

  public Streams(final Options options) {
    this.options = options;
    schemas.put(Inflector.plural(ADDRESS), loadSchema(ADDRESS));
    schemas.put(Inflector.plural(CUSTOMER), loadSchema(CUSTOMER));
    schemas.put(Inflector.plural(EMAIL), loadSchema(EMAIL));
    schemas.put(Inflector.plural(EVENT), loadSchema(EVENT));
    schemas.put(Inflector.plural(PHONE), loadSchema(PHONE));
    schemas.put(Inflector.plural(STREAM), loadSchema(STREAM));
    schemas.put(Inflector.plural(TICKET), loadSchema(TICKET));
    schemas.put(Inflector.plural(VENUE), loadSchema(VENUE));
  }

  public void start() {

    final Properties properties = toProperties(properties());

    final Topology topology = streamsBuilder(options).build(properties);

    log.info("Topology:\n{}", topology.describe());

    final KafkaStreams streams = new KafkaStreams(topology, properties);

    streams.setUncaughtExceptionHandler(e -> {
      log.error("unhandled streams exception, shutting down (a warning of 'Detected that shutdown was requested. All clients in this app will now begin to shutdown' will repeat every 100ms for the duration of session timeout).", e);
      return StreamsUncaughtExceptionHandler.StreamThreadExceptionResponse.SHUTDOWN_APPLICATION;
    });

    streams.start();

    Runtime.getRuntime().addShutdownHook(new Thread(new ShutdownHook(streams)));
  }

  private StreamsBuilder streamsBuilder(final Options options) {

    final var builder = new StreamsBuilder();

    KafkaJsonSchemaSerde serde = new KafkaJsonSchemaSerde();
    serde.configure(Map.ofEntries(Map.entry("schema.registry.url", "http://localhost:8081")), false);


    builder.<String, JsonNode>stream(options.in(), Consumed.as("in"))
            .peek((k, v) -> log.info("key={}, value={}", k, v))
            .processValues(new FixedKeyProcessorSupplier<String, JsonNode, JsonNode>() {
              @Override
              public FixedKeyProcessor<String, JsonNode, JsonNode> get() {
                return new FixedKeyProcessor<>() {
                  private FixedKeyProcessorContext<String, JsonNode> context;
                  @Override
                  public void init(FixedKeyProcessorContext<String, JsonNode> context) {
                    this.context = context;
                  }
                  @Override
                  public void process(FixedKeyRecord<String, JsonNode> fixedKeyRecord) {
                    context.recordMetadata().ifPresent(metadata -> ((ObjectNode) fixedKeyRecord.value()).put(TYPE_FIELD, toType(metadata)));
                    context.forward(fixedKeyRecord);
                  }
                };
              }
            })
            .mapValues((k, v) -> {
              String type = v.get(TYPE_FIELD).asText();
              JsonSchema schema = schemas.get(type);
              return JsonSchemaUtils.envelope(schema, v);
            })
            .peek((k, v) -> log.info("key={}, value={}", k, v), Named.as("peek"))
            .to((s, jsonNodes, recordContext) -> {
              String type = jsonNodes.get(PAYLOAD_FIELD).get(TYPE_FIELD).asText();
              ((ObjectNode) jsonNodes.get(PAYLOAD_FIELD)).remove(TYPE_FIELD);
              return "sample_" + type;
            }, Produced.with(null, serde));

    return builder;
  }

  /**
   * Default properties, they can be overridden, but better defaults expected
   * by the author of this producer for using this application.
   */
  private static final Map<String, Object> DEFAULTS = Map.ofEntries(
          Map.entry(ProducerConfig.BATCH_SIZE_CONFIG, 65_536),
          Map.entry(ProducerConfig.LINGER_MS_CONFIG, 50L),
          Map.entry(ProducerConfig.COMPRESSION_TYPE_CONFIG, "lz4"),
          Map.entry("internal.leave.group.on.close", true),
          Map.entry(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 5000L)
  );

  /**
   * Immutable properties, properties that cannot be changed. For most applications, this is
   * just how the data is serialized.
   */
  private static final Map<String, Object> IMMUTABLES = Map.ofEntries(
          Map.entry(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class.getName()),
          Map.entry(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, JsonSerde.class.getName())
  );

  private Map<String, Object> properties() {
    final Map<String, Object> map = new HashMap<>();
    // set defaults first, so they can be overridden.
    map.putAll(DEFAULTS);
    // pull configuration settings from the environment.
    // Define environment variables with your deployment (helm) scripts.
    map.putAll(KafkaUtil.environmentProperties(CONFIG_PREFIX));
    // ideally all connection information goes in a file that is mounted for the application in a container.
    // this way environment specific settings are never defined by the application developer. At a minium
    // anything with a secret should be loaded from a property; and not pulled from the environment.
    map.putAll(PropertiesUtil.load(CONNECTION_SECRETS));
    // set immutables last, so they cannot be changed.
    map.putAll(IMMUTABLES);
    return map;
  }


  public static Properties toProperties(final Map<String, Object> map) {
    final Properties properties = new Properties();
    properties.putAll(map);
    return properties;
  }


  private static JsonSchema loadSchema(final String type) {
    try {
      final JsonNode node = JsonUtil.objectMapper().readTree(Thread.currentThread().getContextClassLoader().getResourceAsStream(type + ".json"));
      return new JsonSchema(node);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  private static String toType(final org.apache.kafka.streams.processor.api.RecordMetadata metadata) {
    final int pos = metadata.topic().lastIndexOf("-");
    if (pos == -1) {
      return metadata.topic();
    } else {
      return metadata.topic().substring(pos + 1);
    }
  }

}
