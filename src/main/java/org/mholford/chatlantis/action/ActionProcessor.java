package org.mholford.chatlantis.action;

import org.mholford.chatlantis.bot.Bot;
import org.mholford.chatlantis.context.ContextConstants;
import org.mholford.chatlantis.context.FullContext;
import org.mholford.chatlantis.lookup.instruction.Instruction;
import org.mholford.chatlantis.utterance.Utterance;

import java.util.List;
import java.util.Map;

/**
 * The ActionProcessor handles executing an Action and processing the result.  The basic process
 * is:<br/><ul>
 *   <li>Call act() on the Action</li>
 *   <li>Update the Context with the resulting Instructions</li>
 *   <li>Extract what the Bot should say from the Context</li>
 *   <li>Return an ActionResponse containing what should be spoken and the new Context</li>
 * </ul>
 * <br/>
 * ActionProcessors are initialized from a String-String map of properties.
 */
public interface ActionProcessor extends ContextConstants {
  
  /**
   * Process the specified Action in the default manner specified above.
   * @param action Action to be performed
   * @param context Snapshot of current Context
   * @param bot Current bot
   * @param u Current user Utterance
   * @return Response containing spoken output and new Context
   */
  default ActionResponse process(Action action, FullContext context, Bot bot, Utterance u) {
    List<Instruction> instructions = action.act(context, bot);
    context = context.update(instructions);
    return new ActionResponse(extractSpokenResponse(context), context);
  }
  
  /**
   * Figures out what the Bot should say based upon current Context.  By default this
   * just looks for the action.spoken slot in Utterance context
   * @param context Snapshot of current Context
   * @return What should be spoken
   */
  default String extractSpokenResponse(FullContext context) {
    return (String) context.getUtteranceContext().get("/action.spoken");
  }
  
  /**
   * Initialize the ActionProcessor from a String-String map of properties.  Default
   * behavior is no-op.
   * @param props map of properties
   */
  default void init(Map<String, String> props){}
}
