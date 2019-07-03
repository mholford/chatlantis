package org.mholford.chatlantis;

import org.mholford.chatlantis.context.ContextFactory;

import java.util.UUID;

/**
 * Singleton factory class for building new Conversations.  Creating a new
 * Conversation gives it a unique ID and a new Context.  This is usually handled
 * internally by Chatlantis.
 */
public class ConversationFactory {
  private static ConversationFactory INSTANCE;
  
  private ConversationFactory() {}
  
  /**
   * Gets the singleton instance of the factory, creating it if needed
   * @return Singleton instance
   */
  public static ConversationFactory get() {
    if (INSTANCE == null) {
      INSTANCE =new ConversationFactory();
    }
    return INSTANCE;
  }
  
  /**
   * Creates a new Conversation with the specified user.  A random UUID will
   * be generated to serve as Conversation id and a new Context will be created.
   * @param user User who is speaking
   * @return New conversation
   */
  public Conversation createNew(User user) {
    String uuid = UUID.randomUUID().toString();
    return new Conversation(user, ContextFactory.get().newContext(), uuid);
  }
}
