package org.mholford.chatlantis.intent;

import org.mholford.chatlantis.Utils;
import org.mholford.chatlantis.bot.Bot;
import org.mholford.chatlantis.context.ContextConstants;
import org.mholford.chatlantis.context.FullContext;

import java.util.Map;

/**
 * Basic implementation of IntentMatcher which expects the Intent to be spelled out
 * explicitly in Context.  It checks in the "intent.name" slot in the Utterance Context
 * and gives the Intent named there a value of 100% confidence.
 */
public class ExplicitIntentMatcher implements IntentMatcher, ContextConstants, Utils {
  
  @Override
  public Map<Intent, Double> assignIntent(FullContext ctx, Bot bot) {
    String intentName = (String) ctx.get(fmt("$UTT:/%s.name", INTENT));
    Intent intent = bot.getIntents().get(intentName);
    return mapOf(intent, 100d);
  }
}
