package org.msse.demo.common.util;

import org.msse.demo.common.model.Location;

public class DistanceUtil {

  // radius of the earth in meters
  private static final double EARTH_RADIUS = 6371000;

  private DistanceUtil() {
  }

  public static double distance(Location point1, Location point2) {
    return distance(point1.latitude(), point1.longitude(), point2.latitude(), point2.longitude());
  }

  public static double distance(double lat1, double lng1, double lat2, double lng2) {

    double dLat = Math.toRadians(lat2 - lat1);
    double dLng = Math.toRadians(lng2 - lng1);
    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                    Math.sin(dLng / 2) * Math.sin(dLng / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

    return EARTH_RADIUS * c;
  }

}
