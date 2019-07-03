package org.mholford.chatlantis.action;

import org.mholford.chatlantis.bot.Bot;
import org.mholford.chatlantis.context.FullContext;
import org.mholford.chatlantis.lookup.instruction.Instruction;

import java.util.List;
import java.util.Map;

/**
 * Encapsulates action to be taken after the Bot has determined a Response to user Utterance.
 * An Action can be initialized from a String map of properties.  This initialization occurs
 * when the Bot is initialized during Chatlantis startup.
 * <p>
 *   Actions are performed by callback on the act() method.  Implementations can read from
 *   context and assign Instructions to be performed on the Context after the Action has
 *   completed.
 *   <p>NB: Do not write to the Context in the act() method.  Changes will be lost once
 *   the method exits.  Instead, encapsulate Context changes in Instructions
 */
public interface Action {
  /**
   * Performs the action.  Implementations are encouraged to read from Context.  But, any
   * writes to Context will not propagate.  Instead, encapsulate changes to Context in
   * Instructions.  These Instructions are performed by the Bot after the Action has completed.
   * @param ctx Snapshot of current context
   * @param bot Reference to current bot
   * @return List of Instructions to be applied to Context by the Bot after the Action completes
   */
  List<Instruction> act(FullContext ctx, Bot bot);
  
  /**
   * Initialize the Action using parameter map.  This is performed during Chatlantis startup,
   * when the Bot is initialized.
   *
   * @param map String-String map of parameters
   */
  void init(Map<String, String> map);
}
