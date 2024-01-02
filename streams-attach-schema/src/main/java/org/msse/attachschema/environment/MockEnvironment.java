package org.msse.attachschema.environment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class MockEnvironment implements Environment {

  private static final Logger log = LoggerFactory.getLogger(MockEnvironment.class);

  private final Map<String, String> map = new HashMap<>();

  @Override
  public Map<String, String> environment() {
    log.warn("environment() in mock mode.");
    return map;
  }

  @Override
  public String environment(String name) {
    log.warn("environment() in mock mode.");
    return map.get(name);
  }

  public void set(String name, String value) {
    this.map.put(name, value);
  }

  public void remove(String name) {
    this.map.remove(name);
  }

  public void clear() {
    this.map.clear();
  }

}
