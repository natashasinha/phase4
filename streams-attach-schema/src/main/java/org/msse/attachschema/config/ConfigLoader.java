package org.msse.attachschema.config;

import org.apache.commons.lang3.StringUtils;
import org.msse.attachschema.environment.EnvironmentProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ConfigLoader {

  private static final Logger log = LoggerFactory.getLogger(ConfigLoader.class);

  private static final Pattern PATTERN = Pattern.compile("(?<=[a-z])[A-Z]");

  private static final Map<Class<?>, Function<String, ?>> SIMPLE_CONVERTERS = Map.ofEntries(
          Map.entry(Boolean.TYPE, ConfigLoader::toBoolean),
          Map.entry(Boolean.class, ConfigLoader::toBoolean),
          Map.entry(Integer.TYPE, Integer::parseInt),
          Map.entry(Integer.class, Integer::parseInt),
          Map.entry(Long.TYPE, Long::parseLong),
          Map.entry(Long.class, Long::parseLong),
          Map.entry(Double.TYPE, Double::parseDouble),
          Map.entry(Double.class, Double::parseDouble),
          Map.entry(Float.TYPE, Float::parseFloat),
          Map.entry(Float.class, Float::parseFloat),
          Map.entry(List.class, l -> Arrays.asList(l.trim().split("\\s*,\\s*"))),
          Map.entry(Map.class, ConfigLoader::toMap),
          Map.entry(Duration.class, Duration::parse),
          Map.entry(File.class, File::new)
  );


  private ConfigLoader() {
  }

  public static <T> void populate(final T object) {
    populate(null, object);
  }

  public static <T> void populate(final String prefix, final T object) {
    Class<?> clazz = object.getClass();
    while (!clazz.equals(Object.class)) {
      populateBySetterMethods(prefix(prefix), object, clazz);
      clazz = clazz.getSuperclass();
    }
  }

  private static String prefix(final String string) {
    if (StringUtils.isBlank(string)) {
      return "";
    } else if (string.endsWith("_")) {
      return string;
    } else {
      return string + "_";
    }
  }

  /**
   * for a given variable name determine the environment variable that would
   * be used to set this variable. Such ase, fooBar becomes FOO_BAR and
   * simpleService becomes SIMPLE_SERVICE. If a prefix is defined, such as APP,
   * then the results would be APP_FOO_BAR and APP_SIMPLE_SERVICE.
   */
  private static String getEnvironmentVariable(final String prefix, final String string) {
    return prefix + PATTERN.matcher(string).replaceAll(match -> "_" + match.group()).toUpperCase();
  }

  private static String getEnvironment(final String environment) {
    return EnvironmentProvider.getEnvironment().environment(environment);
  }

 private static Map<String, String> getEnvironmentsByPrefix(final String prefix) {
    return EnvironmentProvider.getEnvironment().environment().entrySet().stream()
            .filter(entry -> entry.getKey().startsWith(prefix))
            .collect(Collectors.toMap(e -> e.getKey().substring(prefix.length()), Map.Entry::getValue));
  }

  /**
   * An alternate method would be to populate by field name, instead of setter methods. In many ways that is
   * cleaner, but since most fields are private it requires setting assessable to true; which can flag various
   * organizations security checks. Thus, the approach shown here is using public setters. But getDeclaredMethods()
   * could easily be replaced with getDeclaredFields() and then calling setAssessable(true) on that field.
   */
  private static <T> void populateBySetterMethods(final String prefix, final T object, final Class<?> clazz) {

    log.debug("loading config class {}", clazz.getName());

    Stream.of(clazz.getDeclaredMethods())
            .filter(m -> !Modifier.isStatic(m.getModifiers()))
            .filter(m -> Modifier.isPublic(m.getModifiers()))
            .filter(m -> m.getParameterCount() == 1)
            .forEach(m -> {
              final String name = extractName(m.getName());
              final Class<?> type = m.getParameters()[0].getType();

              final String environment = getEnvironmentVariable(prefix, name);

              final String value = getEnvironment(environment);

              log.info("using env={} for method={}({})", environment, m.getName(), type.getSimpleName());

              Object convertedValue = convert(type, value);
              if (convertedValue == null && Map.class.equals(type)) {
                convertedValue = getEnvironmentsByPrefix(environment + "_");
              }

              if (convertedValue != null) {
                log.debug("setting field={} type={} env={}", m.getName(), type.getSimpleName(), environment);
                try {
                  m.invoke(object, convertedValue);
                } catch (final IllegalAccessException | InvocationTargetException e) {
                  throw new ConfigException(e);
                }
              }

            });
  }


  /**
   * Convert the data type to the expected class type that is expected on the configuration class.
   */
  private static Object convert(final Class<?> type, final String value) {

    if (value == null) {
      return null;
    }

    // most of the converts are straight forward and part of this map of functions.
    final Function<String, ?> f = SIMPLE_CONVERTERS.get(type);

    if (f != null) {
      return f.apply(value);
    } else if (Enum.class.isAssignableFrom(type)) {
      return create(type, value);
    } else if (!type.isPrimitive()) {
      // handles any class where a constructor that takes a string is available.
      try {
        final Constructor<?> constructor = type.getConstructor(String.class);
        return constructor.newInstance(value);
      } catch (final InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
        // if this gets executed, either implement support or change your configuration class to have a different type.
        throw new ConfigException(String.format("unsupported type=%s.", type), e);
      }
    } else {
      // if this gets executed, either implement support or change your configuration class to have a different type.
      throw new ConfigException(String.format("unsupported type=%s.", type));
    }
  }

  /**
   * fooBar() maps to fooBar, setFooBar() maps to fooBar.
   */
  private static String extractName(final String name) {
    final String prefix = "set";
    if (name.startsWith(prefix) && name.length() > prefix.length()) {
      return Character.toLowerCase(name.charAt(prefix.length())) + name.substring(prefix.length() + 1);
    } else {
      return name;
    }
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private static Enum<?> create(final Class<?> type, final String value) {
    return Enum.valueOf((Class<Enum>) type, value);
  }

  private static boolean toBoolean(String string) {
    if (string == null) {
      return false;
    }
    return ("true".equalsIgnoreCase(string) || "t".equalsIgnoreCase(string));
  }

  private static Map<String, Object> toMap(final String string) {
    Properties properties = toProperties(string.replace("\\|", "\n"));
    return new HashMap<>(properties.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().toString(), Map.Entry::getValue)));
  }

  private static Properties toProperties(final String string) {

    final Properties properties = new Properties();

    try {
      if (StringUtils.isNotBlank(string)) {
        properties.load(new StringReader(string));
      }
    } catch (IOException e) {
      // will not happen from a non-null string.
    }

    return properties;
  }

}
