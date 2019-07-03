package org.mholford.chatlantis.utterance;

/**
 * Singleton factory class used to create Tokens.  The Utterance
 * factory uses this class to create new Tokens within a new
 * Utterance, so it should rarely need to be used in client code.
 */
public class TokenFactory {
  private static TokenFactory INSTANCE;
  
  private TokenFactory(){}
  
  /**
   * Gets the singleton instance of the token factory, creating it
   * if necessary
   * @return The singleton instance
   */
  public static TokenFactory get() {
    if (INSTANCE == null) {
      INSTANCE = new TokenFactory();
    }
    return INSTANCE;
  }
  
  /**
   * Creates an EntityToken with the specified alias and literal value
   * @param alias Alias of token
   * @param value Literal value
   * @return A new EntityToken
   */
  public EntityToken createEntity(String alias, String value) {
    return new EntityToken(alias, value);
  }
  
  /**
   * Creates a LiteralToken with the specified value
   * @param value literal value
   * @return A new LiteralToken
   */
  public LiteralToken createLiteral(String value) {
    return new LiteralToken(value);
  }
}
