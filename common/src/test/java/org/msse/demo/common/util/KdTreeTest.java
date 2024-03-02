package org.msse.demo.common.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KdTreeTest {

  static class DataPoint extends KdTree.XYZPoint {

    private String name;

    public DataPoint(double x, double y, double z, String name) {
      super(x, y);
      this.name = name;
    }

    public String name() {
      return name;
    }

    @Override
    public String toString() {
      return "DataPoint{" +
              "name='" + name + '\'' +
              ", x=" + x +
              ", y=" + y +
              ", z=" + z +
              '}';
    }
  }

  @Test
  void nearest() {

    KdTree<DataPoint> tree = new KdTree<>();

    data().forEach(tree::add);

    Collection<DataPoint> points = tree.nearestNeighbourSearch(1, new DataPoint(44.8849, -93.2131, 0, null));

    Assertions.assertEquals("U.S. Bank Stadium", points.iterator().next().name);

    points = tree.nearestNeighbourSearch(4, new DataPoint(45.0, 0.0, 0, "X"));

    System.out.println(points);

  }

  @Test
  void x() {

    //44.8849° N, 93.2131° W

    //51.4680° N, 0.4551° W

    //4001 mi
    //6438985

    DataPoint msp = new DataPoint(44.8849,-93.2131, 0.0, "msp");
    DataPoint lhr = new DataPoint(51.4580,-0.4551, 0.0, "lhr");

    ArrayList<KdTree.XYZPoint> list = new ArrayList<>();
    list.add(lhr);

    KdTree<DataPoint> tree = new KdTree<>(list);

    System.out.println(tree.nearestNeighbourSearch(1, msp));
  }

  private static List<DataPoint> data() {

    final List<DataPoint> list = new ArrayList<>();

    try {
      CSVFormat format = CSVFormat.Builder.create().setDelimiter(",").setHeader().build();

      InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("venues.csv");
      InputStreamReader reader = new InputStreamReader(input);

      CSVParser parser = new CSVParser(reader, format);

      //name,street,city,state,zip,lat,lat,capacity

      for (CSVRecord rec : parser) {

        DataPoint point = new DataPoint(Double.parseDouble(rec.get("lat")), Double.parseDouble(rec.get("lon")), 0.0, rec.get("name"));

        list.add(point);

      }

      parser.close();

    } catch (final IOException e) {
      throw new RuntimeException(e);
    }

    return list;
  }

}