package org.mholford.chatlantis.intent;

import org.mholford.chatlantis.bot.Bot;
import org.mholford.chatlantis.context.FullContext;

import java.util.Map;

/**
 * Defines how Chatlantis matches Utterances to Intents.  Matchers can be initialized
 * from a Map of parameters.  Intent Matchers will use information in the current Context
 * snapshot to decide which Intents are relatively more germane.  A map of Intent ->
 * confidence score is returned, allowing Chatlantis to pick from multiple possible
 * Intents.
 * <p>
 *   <b>NB:</b> Intent Matchers must only read content from the context snapshot.  Any changes
 *   made to Context will not be preserved.
 * </p>
 */
public interface IntentMatcher {
  /**
   * Assigns Intents with level of confidence based upon what's in the FullContext snapshot
   * @param ctx Context to read from
   * @param bot Current bot
   * @return Map of Intent -> confidence score
   */
  Map<Intent, Double> assignIntent(FullContext ctx, Bot bot);
  
  /**
   * Initializes the IntentMatcher based on parameters provided.  By default, this is a no-op.
   * @param map Map of parameters
   */
  default void init(Map<String, String> map){}
}
