package org.mholford.chatlantis.context;

/**
 * Singleton factory class used to create Contexts.  Creation of Contexts
 * is handled by Chatlantis transparently during creation of Users,
 * Conversations and Utterances.
 */
public class ContextFactory implements ContextConstants {
  private static ContextFactory INSTANCE;
  
  private ContextFactory() {}
  
  /**
   * Gets the singleton instance of the ContextFactory
   * @return Singleton instance
   */
  public static ContextFactory get() {
    if (INSTANCE == null) {
      INSTANCE = new ContextFactory();
    }
    return INSTANCE;
  }
  
  /**
   * Creates a new Context backed by an empty HashTreePMap
   * @return New, empty Context
   */
  public final Context newContext() {
    return new Context(EMPTY_MAP);
  }
}
