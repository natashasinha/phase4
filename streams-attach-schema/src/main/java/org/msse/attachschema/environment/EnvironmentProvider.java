package org.msse.attachschema.environment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EnvironmentProvider {

  private static final Logger log = LoggerFactory.getLogger(EnvironmentProvider.class);

  private static Environment environment = new SystemEnvironment();

  private EnvironmentProvider() {
  }

  public static Environment getEnvironment() {
    return environment;
  }

  /**
   * Package Scope, public method for this is in test package so it is never accidentally
   * enabled from non-test code.
   */
  static MockEnvironment enableMockMode() {

    if (environment instanceof MockEnvironment) {
      log.info("EnvironmentProvider already in test mode, returning existing mock environment.");
      return (MockEnvironment) EnvironmentProvider.environment;
    }

    log.warn("putting EnvironmentProvider into test mode. This should only happen in tests.");
    MockEnvironment mockEnvironment = new MockEnvironment();
    EnvironmentProvider.environment = mockEnvironment;
    return mockEnvironment;
  }

  static void disableMockMode() {
    if (EnvironmentProvider.environment instanceof SystemEnvironment) {
      return;
    }
    EnvironmentProvider.environment = new SystemEnvironment();
  }
}
