package org.msse.attachschema.config;

import org.msse.attachschema.environment.EnvironmentProvider;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * With containers, environment based configuration is super convenient.
 */
public final class KafkaUtil {

    private KafkaUtil() {
    }

    /**
     * Takes all environment variables that start with the given prefix, and return them with the key modified
     * to exclude the prefix, be lower-case, and replace '_' with '.'. This works extremely well for Apache
     * Kafka clients that only use '.' as a delimiter, and environment variables typically use '_'.
     */
    public static Map<String, String> environmentProperties(final String prefix) {
        return EnvironmentProvider.getEnvironment().environment().entrySet().stream()
                .filter(e -> e.getKey().startsWith(prefix))
                .map(e -> {
                    final String key = e.getKey().substring(prefix.length()).replace("_", ".").toLowerCase();
                    return Map.entry(key, e.getValue());
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
