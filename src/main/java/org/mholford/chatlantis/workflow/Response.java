package org.mholford.chatlantis.workflow;

import org.mholford.chatlantis.action.Action;
import org.mholford.chatlantis.context.FullContext;
import org.mholford.chatlantis.lookup.instruction.Instruction;

import java.util.List;

/**
 * Represents a Response to an Utterance in Chatlantis.  The Response is returned from the
 * Intent Resolver and contains the following fields: <ul>
 *   <li>spokenResponse - What Chatlantis will "say"</li>
 *   <li>actions - Actions to be performed</li>
 *   <li>instructions - Changes to be made to Context</li>
 *   <li>context - Current context snapshot</li>
 *   <li>score - Score for the Response</li>
 * </ul>
 * Typically, a Workflow will evaluate several Responses and pick the "best", i.e. the one
 * with the highest score.  After getting this "best" Response from the executing Workflow,
 * the Bot is responsible for performing the context changes and actions and responding to
 * the user.  This class is functionally immutable.  New instances should be created by
 * the ResponseFactory class. This is handled internally as part of normal Chatlantis
 * utterance processing.
 * <p>
 *   <b>NB:</b> the context snapshot should be treated as read only.  Any changes to Context
 *   should be specified as Instructions to be performed at a later time.
 * </p>
 */
public class Response {
  private final String spokenResponse;
  private final List<Action> actions;
  private final List<Instruction> instructions;
  private final FullContext context;
  private final double score;
  
  Response(String spokenResponse, List<Action> actions, List<Instruction> instructions,
           FullContext context, double score) {
    this.spokenResponse = spokenResponse;
    this.actions = actions;
    this.instructions = instructions;
    this.context = context;
    this.score = score;
  }
  
  /**
   * Gets the Context snapshot for this Reponse
   * @return Context snapshot
   */
  public FullContext getContext() {
    return context;
  }
  
  /**
   * Gets what should be "spoken" by Chatlantis
   * @return Spoken response
   */
  public String getSpokenResponse() {
    return spokenResponse;
  }
  
  /**
   * Gets the actions that should be performed by the Bot
   * @return List of actions
   */
  public List<Action> getActions() {
    return actions;
  }
  
  /**
   * Gets the changes to Context that should be made by the bot
   * @return List of instructions
   */
  public List<Instruction> getInstructions() {
    return instructions;
  }
  
  /**
   * Gets the score measuring effectiveness of Response
   * @return score
   */
  public double getScore() {
    return score;
  }
}
