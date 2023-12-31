package org.msse.demo.common.util;

import org.junit.jupiter.api.Test;
import org.msse.demo.common.model.Location;

import static org.junit.jupiter.api.Assertions.*;

class DistanceUtilTest {

  private static final double METERS_TO_MILES = 1609.34;

  // MSP Airport Minneapolis/St. Paul, MN, USA
  private final Location msp = new Location(44.8849, -93.2131);

  // Heathrow Airport London, UK
  private final Location lhr = new Location(51.4680, -0.4551);

  @Test
  void testCalculateDistance() {
    // 4002.60 as provided by distance.to
    assertEquals(4002.60, DistanceUtil.distance(msp, lhr)/METERS_TO_MILES, 1.0);
  }
}