package org.msse.attachschema.environment;

import java.util.Map;

class SystemEnvironment implements Environment {

  public Map<String, String> environment() {
    return System.getenv();
  }

  public String environment(final String name) {
    return System.getenv(name);
  }

}
