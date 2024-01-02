package org.msse.attachschema.environment;

import java.util.Map;

public interface Environment {

  Map<String, String> environment();

  String environment(String name);

}
