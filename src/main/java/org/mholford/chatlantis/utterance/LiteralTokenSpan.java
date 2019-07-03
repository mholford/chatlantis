package org.mholford.chatlantis.utterance;

import org.mholford.chatlantis.Utils;

import java.util.List;

/**
 * Represents a consecutive span of Literal Tokens.  It is composed
 * of a start position within the Utterance and a list of the
 * literal tokens.  A convenience method (asString) outputs these
 * tokens as a String.  This class is effectively immutable.
 */
public class LiteralTokenSpan implements Utils {
  private final int start;
  private final List<LiteralToken> tokens;
  
  /**
   * Creates a LiteralTokenSpan with the specified start position
   * and list of tokens
   * @param start Start position in Utterance
   * @param tokens Tokens in the span
   */
  public LiteralTokenSpan(int start, List<LiteralToken> tokens) {
    this.start = start;
    this.tokens = tokens;
  }
  
  /**
   * Gets the start position within the Utterance
   * @return start pos
   */
  public int getStart() {
    return start;
  }
  
  /**
   * Gets the list of tokens comprising this span
   * @return List of tokens
   */
  public List<LiteralToken> getTokens() {
    return tokens;
  }
  
  /**
   * Outputs the list of tokens as a single string by concatenating their
   * literal values
   * @return String
   */
  public String asString() {
    List<String> literals = map(tokens, LiteralToken::getValue);
    return String.join(" ", literals);
  }
}
