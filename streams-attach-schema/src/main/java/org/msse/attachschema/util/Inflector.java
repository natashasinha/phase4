package org.msse.attachschema.util;

import java.util.Arrays;
import java.util.List;

/**
 * Not A complete solution, but works for all of the nouns used in the examples here. It also shows
 * how to address words that are already plural, in case it needs to be extended.
 */
public final class Inflector {

  private static final List<String> SINGULAR_IS_ALSO_PLURAL =
          Arrays.asList("aircraft", "fish", "species", "sheep", "deer", "series", "offspring", "salmon", "shrimp", "trout", "headquarters", "means", "crossroads");

  public static String plural(final String word) {

    if (word.length() < 2) {
      return word;
    }

    if (SINGULAR_IS_ALSO_PLURAL.contains(word)) {
      return word;
    }

    final String lastLetter = word.substring(word.length() - 1);
    final String lastTwoLetters = word.substring(word.length() - 2);

    if (lastLetter.matches("[sxo]") || lastTwoLetters.matches("(sh|ch)")) {
      return word + "es";
    } else {
      return word + "s";
    }
  }
//  public static String singular(final String word) {
//
//    if (word.length() < 3) {
//      return word;
//    }
//
//    if (SINGULAR_IS_ALSO_PLURAL.contains(word)) {
//      return word;
//    }
//
//    final String lastLetter = word.substring(word.length() - 1);
//    final String lastTwoLetters = word.substring(word.length() - 2);
//    final String lastThreeLetters = word.substring(word.length() - 3);
//
//    if (lastLetter.equals("s") && !lastTwoLetters.equals("es")) {
//      return word.substring(0, word.length() - 1);
//    } else if (lastTwoLetters.equals("es")) {
//      return word.substring(0, word.length() - 2);
//    }
//
//    return word;
//  }

}
