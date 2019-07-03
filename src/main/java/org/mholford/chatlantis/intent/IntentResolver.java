package org.mholford.chatlantis.intent;

import org.mholford.chatlantis.bot.Bot;
import org.mholford.chatlantis.context.FullContext;
import org.mholford.chatlantis.workflow.Response;

import java.util.Map;

/**
 * Defines how an Intent is resolved into a Response.  The Response will contain
 * next spoken lines for the Bot as well as Actions to perform.  The Resolver will
 * typically use information from the Context snapshot to determine whether an
 * Intent has been reached and what steps to take next.  The Resolver can optionally
 * be initialized from a Map of properties.  Initialization of IntentResolvers occurs
 * during initialization of Chatlantis.
 */
public interface IntentResolver {
  /**
   * Resolves the current Intent into a Chatlantis Response (spoken response + actions)
   * @param intent Intent to resolve against
   * @param bot Current bot
   * @param ctx Snapshot of Context
   * @return Populated Response for Chatlantis
   */
  Response resolve(Intent intent, Bot bot, FullContext ctx);
  
  /**
   * Initializes the IntentResolver from the specified map of properties.  By default,
   * this is a no-op method.
   * @param map Map of properties.
   */
  default void init(Map<String, String> map){}
}
