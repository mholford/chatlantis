package org.mholford.chatlantis.utterance;

import org.mholford.chatlantis.Utils;

/**
 * Represents a literal String token.  The literal
 * value is used directly in lookup.  These should
 * be constructed by calling TokenFactory.get().
 * createLiteral()
 */
public class LiteralToken implements Token, Utils {
  private final String value;
  
  LiteralToken(String value) {
    this.value = value;
  }
  
  @Override
  public String getValue() {
    return value;
  }
  
  @Override
  public String toLookupString() {
    return value;
  }
  
  @Override
  public String toString() {
    return fmt("L(%s)", value);
  }
}
