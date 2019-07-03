package org.mholford.chatlantis.lookup;

import org.mholford.chatlantis.Utils;

import java.util.EnumSet;
import java.util.List;

/**
 * Represents a "phrase" within a LUT generation clause.  The phrase is the lowest
 * level at which literal elements can be represented.  A phrase is composed of a name,
 * a set of attributes and a list of variants.  These variants are different ways of
 * "saying" the phrase.  The strings can be multiple-word, but typically they will be
 * single words or short phrases.  During permutation, entries for each variant will
 * be created.  This class is effectively immutable, as all fields are final.
 */
public class PhraseConf implements Permutable, Utils {
  private final List<String> variants;
  private final String name;
  private final EnumSet<GenAttr> attributes;
  
  /**
   * Creates a new PhraseConf with the specified name, list of variants and set of
   * attributes
   * @param name Name of phrase
   * @param variants Ways of saying the phrase
   * @param attributes Attributes of phrase
   */
  public PhraseConf(String name, List<String> variants, EnumSet<GenAttr> attributes) {
    this.name = name;
    this.variants = variants;
    this.attributes = attributes;
  }
  
  @Override
  public List<ClausePermutation> getPermutations() {
    return map(variants, v -> new ClausePermutation(v));
  }
  
  @Override
  public EnumSet<GenAttr> getAttributes() {
    return attributes;
  }
  
  /**
   * Gets the name of the phrase
   * @return Name of phrase
   */
  public String getName() {
    return name;
  }
}
