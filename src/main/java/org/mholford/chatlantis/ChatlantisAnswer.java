package org.mholford.chatlantis;

/**
 * POJO holding a spoken chatlantis response and a reference to the Conversation it
 * is from.  The Conversation reference allow future Utterances to say which Conversation
 * they are part of.  This class is functionally immutable.
 */
public class ChatlantisAnswer {
  private final String answer;
  private final Conversation conversation;
  
  /**
   * Creates a new ChatlantisAnswer with the specified spoken part and Conversation reference
   * @param answer spoken response
   * @param conversation reference to conversation
   */
  public ChatlantisAnswer(String answer, Conversation conversation) {
    this.answer = answer;
    this.conversation = conversation;
  }
  
  /**
   * Gets the spoken part of the response
   * @return Spoken response
   */
  public String getAnswer() {
    return answer;
  }
  
  /**
   * Gets the conversation this response refers to
   * @return conversation reference
   */
  public Conversation getConversation() {
    return conversation;
  }
  
  @Override
  public String toString() {
    return answer;
  }
}
