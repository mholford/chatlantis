package org.mholford.chatlantis.utterance;

/**
 * Token is the base unit from which Utterances are composed.
 * Currently, we define two types of Token: <ul>
 *   <li>LiteralToken - represents a literal string</li>
 *   <li>EntityToken - represents an extracted entity</li>
 * </ul>
 * Tokens have both a literal value and a value that is used
 * when looking up an Utterance in the Lookup table.  For Literal
 * tokens, these values are the same.  For Entity tokens, the
 * literal value is replaced by a Marker in the Lookup string.
 * Tokens should always be created by the singleton TokenFactory.
 */
public interface Token {
  /**
   * Gets the literal value of the token
   * @return Literal value
   */
  String getValue();
  
  /**
   * Gets the value of the token to be used in a lookup string
   * @return Lookup string value
   */
  String toLookupString();
}
