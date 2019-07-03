package org.mholford.chatlantis.workflow;

import org.mholford.chatlantis.action.Action;
import org.mholford.chatlantis.context.FullContext;
import org.mholford.chatlantis.lookup.instruction.Instruction;

import java.util.List;

/**
 * Singleton factory to create instances of Response.  This is usually handled internally
 * as part of normal Chatlantis Utterance processing.
 */
public class ResponseFactory {
  private static ResponseFactory INSTANCE;
  
  private ResponseFactory() {
  }
  
  /**
   * Gets the singleton instance, creating it if necessary
   * @return Factory instance
   */
  public static ResponseFactory get() {
    if (INSTANCE == null) {
      INSTANCE = new ResponseFactory();
    }
    return INSTANCE;
  }
  
  /**
   * Creates a new Response from the specified information
   * @param spoken What Chatlantis bot should "say"
   * @param actions List of actions to be performed by Bot
   * @param instructions List of Context changes to be made by Bot
   * @param context Snapshot of Context
   * @param score Score of response
   * @return Fully instantiated Response
   */
  public Response createResponse(String spoken, List<Action> actions,
                                 List<Instruction> instructions, FullContext context,
                                 double score) {
    
    return new Response(spoken, actions, instructions, context, score);
  }
}
