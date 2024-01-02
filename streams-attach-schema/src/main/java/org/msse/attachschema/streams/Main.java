package org.msse.attachschema.streams;


import org.msse.attachschema.config.ConfigLoader;

public class Main {

  public static void main(String[] args) {

    final Options options = new Options();

    ConfigLoader.populate(options);

    final Streams stream = new Streams(options);

    stream.start();
  }
}

