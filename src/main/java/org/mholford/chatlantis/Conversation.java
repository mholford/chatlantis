package org.mholford.chatlantis;

import org.mholford.chatlantis.context.Context;

/**
 * Represents a Conversation in Chatlantis.  It is composed of a unique id,
 * a reference to the speaker (users) and a Context.  Altough multiple
 * Utterances occur within a Conversation, the Conversation does not keep
 * a record of Utterances.  Rather, any important information from earlier
 * in the Conversation should be stored in the Context.  Conversations should
 * be created by the singelton ConversationFactory class.  This is done
 * internally by Chatlantis when initiating a new Conversation.
 */
public class Conversation {
  private final User user;
  private Context context;
  private final String id;
  
  Conversation(User user, Context context, String id) {
    this.user = user;
    this.context = context;
    this.id = id;
  }
  
  /**
   * Gets the User who is speaking in a Conversation
   * @return User
   */
  public User getUser() {
    return user;
  }
  
  /**
   * Gets the unique identifier of the Conversation
   * @return unique id
   */
  public String getId() {
    return id;
  }
  
  /**
   * Gets the Context of the Conversation
   * @return Context object
   */
  public Context getContext() {
    return context;
  }
  
  /**
   * Sets the context of the Conversation to the specified value
   * @param context Context of Conversation
   */
  public void setContext(Context context) {
    this.context = context;
  }
}
