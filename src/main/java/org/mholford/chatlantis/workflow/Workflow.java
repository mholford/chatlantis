package org.mholford.chatlantis.workflow;

import com.google.common.base.Strings;
import org.mholford.chatlantis.Conversation;
import org.mholford.chatlantis.Utils;
import org.mholford.chatlantis.bot.Bot;
import org.mholford.chatlantis.context.Context;
import org.mholford.chatlantis.context.FullContext;
import org.mholford.chatlantis.intent.Intent;
import org.mholford.chatlantis.intent.IntentMatcher;
import org.mholford.chatlantis.intent.IntentResolver;
import org.mholford.chatlantis.lookup.instruction.Instruction;
import org.mholford.chatlantis.prompt.PromptHandler;
import org.mholford.chatlantis.utterance.Utterance;

import java.io.IOException;
import java.util.*;

/**
 * The primary workhorse of the Chatlantis utterance processing framework, Workflow implementations
 * are responsible for determining the best Response to a user Utterance.  Additionally, the
 * Workflow instance maintains lists of helper classes (UtteranceProcessors, IntentMatchers,
 * IntentResolvers and PromptHandlers).  It has methods to select from the these components
 * to pick the best for the Utterance at hand.  (At present, these methods simply return the
 * one and only instance of each class; but the adaptability is present to address future needs).
 * DefaultWorkflow offers a basic implementation of this class.  Customizations should extend
 * that class.
 * <p>
 *   The processing occurs as follows:<ol>
 *     <li>The UtteranceProcessors are executed sequentially, with the output(s) of a
 *     processor used to supply input for the next.  After going through the processor chain,
 *     we are left with a list of utterances, each representing a possible "interpretation"
 *     of the original input</li>
 *     <li>These "interpretations" are analyzed to determine which is most appropriate given
 *     the current situation.  That process is as follows:<ul>
 *       <li>The utterance is looked up against the FST lookup table</li>
 *       <li>If matched, the resulting instructions are executed against the Context snapshot</li>
 *       <li>An IntentMatcher is selected</li>
 *       <li>The IntentMatcher assigns an Intent</li>
 *       <li>The PromptHandler is activate if applicable</li>
 *       <li>An IntentResolver is selected</li>
 *       <li>A Response (with score) is generated by attempting to resolve the Intent</li>
 *     </ul></li>
 *     <li>The best Response is determined.  At present, this will be the one with the highest
 *     score; the score being the percentage of constraints on the Intent that were met.  If
 *     there is no suitable Response, this will return a simple spoken response to say that
 *     the Utterance was not understood.</li>
 *   </ol>
 * </p>
 */
public interface Workflow extends Utils {
  default Response process(Utterance input, Conversation conv, Bot bot) throws IOException {
    Set<Utterance> utts = new HashSet<>();
    utts.add(input);
    for (UtteranceProcessor up : getProcessors()) {
      Set<Utterance> newUtts = new HashSet<>();
      for (Utterance u : utts) {
        List<Utterance> results = up.process(u);
        newUtts.addAll(results);
      }
      utts = newUtts;
    }
    
    List<Response> candidateResponses = new ArrayList<>();
    Context convContext = conv.getContext();
    Context userContext = conv.getUser().getContext();
    for (Utterance u : utts) {
      FullContext ctxSnapshot = new FullContext(userContext, convContext, u.getContext());
      String ctxUpdate = bot.getLookupTable().lookup(u);
      
      if (!Strings.isNullOrEmpty(ctxUpdate)) {
        List<Instruction> instructions = parseInstructions(ctxUpdate);
        ctxSnapshot = ctxSnapshot.update(instructions);
        IntentMatcher im = pickBestIntentMatcher(u);
        Map<Intent, Double> intentDoubleMap = im.assignIntent(ctxSnapshot, bot);
        Intent intent = topEntry(intentDoubleMap).getKey();
        if (intent == null) {
          continue;
        }
        PromptHandler ph = pickBestPromptHandler(intent, u);
        ctxSnapshot = ph.handlePrompt(ctxSnapshot);
        IntentResolver ir = pickBestIntentResolver(intent, u);
        Response r = ir.resolve(intent, bot, ctxSnapshot);
        candidateResponses.add(r);
      }
    }
    return pickBestResponse(candidateResponses, input, conv);
  }
  
  /**
   * Picks the best Response for the Utterance by selecting the one with the highest score (i.e.
   * highest percentage of constraints on Intent met).  If there are no appropriate Responses,
   * it will return a simple one saying "Sorry, I didn't understand".
   * @param candidates Possible Responses
   * @param input Original utterance
   * @param conv Conversation
   * @return Best Response
   */
  default Response pickBestResponse(List<Response> candidates, Utterance input, Conversation conv) {
    if (candidates.size() <= 0) {
      return ResponseFactory.get().createResponse("Sorry, I didn't understand",
          Collections.emptyList(), null,
          new FullContext(conv.getUser().getContext(), conv.getContext(), input.getContext()), 0d);
    }
    candidates.sort(Comparator.comparing(Response::getScore).reversed());
    return candidates.get(0);
  }
  
  /**
   * Picks the IntentMatcher most appropriate for the Utterance.  Presently, returns the first
   * configured IntentMatcher.
   * @param u Current Utterance
   * @return IntentMatcher
   */
  default IntentMatcher pickBestIntentMatcher(Utterance u) {
    return getMatchers().get(0);
  }
  
  /**
   * Picks the IntentResolver most appropriate for the Utterance and Intent.
   * Presently, returns the first configured IntentResolver
   * @param intent Current intent
   * @param u Current Utterance
   * @return IntentResolver
   */
  default IntentResolver pickBestIntentResolver(Intent intent, Utterance u) {
    return getResolvers().get(0);
  }
  
  /**
   * Picks the PromptHandler most appropriate for the Utterance and Intent.
   * Presently, returns the first configured PromptHandler.
   * @param intent Current intent
   * @param u Current Utterance
   * @return Prompt Handler
   */
  default PromptHandler pickBestPromptHandler(Intent intent, Utterance u) {
    return getPromptHandlers().get(0);
  }
  
  /**
   * Gets all Utterance Processor registered with this Workflow
   * @return list of processors
   */
  List<UtteranceProcessor> getProcessors();
  
  /**
   * Gets all IntentMatchers registered with this Workflow
   * @return List of matchers
   */
  List<IntentMatcher> getMatchers();
  
  /**
   * Gets all IntentResolvers registered with this Workflow
   * @return List of resolvers
   */
  List<IntentResolver> getResolvers();
  
  /**
   * Gets all PromptHandlers registered with this Workflow
   * @return List of prompt handlers
   */
  List<PromptHandler> getPromptHandlers();
  
  /**
   * Gets the name of this Workflow
   * @return name of workflow
   */
  String getName();
}
