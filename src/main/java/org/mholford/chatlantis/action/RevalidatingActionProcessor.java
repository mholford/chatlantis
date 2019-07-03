package org.mholford.chatlantis.action;

import com.google.common.base.Strings;
import org.mholford.chatlantis.Utils;
import org.mholford.chatlantis.bot.Bot;
import org.mholford.chatlantis.context.FullContext;
import org.mholford.chatlantis.intent.Intent;
import org.mholford.chatlantis.intent.IntentResolver;
import org.mholford.chatlantis.lookup.instruction.Instruction;
import org.mholford.chatlantis.lookup.instruction.InstructionContext;
import org.mholford.chatlantis.lookup.instruction.RemoveSlot;
import org.mholford.chatlantis.utterance.Utterance;
import org.mholford.chatlantis.workflow.Response;

import java.util.List;

/**
 * Implementation of ActionProcessor which revalidates Intent if the intent has changed
 * during the processing of the Utterance.
 */
public class RevalidatingActionProcessor implements ActionProcessor, Utils {
  /**
   * Overrides default behavior to detect if the Intent of the Utterance changed as a
   * result of processing.  If so, the Processor will revalidate the Intent, putting the
   * results of that validation in the Context so it can inform the Bot's next output.
   *
   * @param action  Action to be performed
   * @param context Snapshot of current Context
   * @param bot     Current bot
   * @param u       Current user Utterance
   * @return Response to Action
   */
  @Override
  public ActionResponse process(Action action, FullContext context, Bot bot, Utterance u) {
    // find Intent before performing Action
    String currIntent = (String) context.get("$utt:/intent.name");
    
    // Perform Action and update Context
    List<Instruction> instructions = action.act(context, bot);
    context = context.update(instructions);
    
    // find Intent after performing Action
    String newIntent = (String) context.get("$utt:/intent.name");
    
    // If Intent is different, re-resolve:
    // 1. Invoke the IntentResolver and process its Response
    // 2. Process whatever Actions derive from this Response
    if (currIntent != null && !currIntent.equals(newIntent)) {
      Intent intent = bot.getIntent(newIntent);
      IntentResolver ir = bot.pickWorkflow(u).pickBestIntentResolver(intent, u);
      Response resp = ir.resolve(intent, bot, context);
      FullContext newCtx = resp.getContext();
      newCtx = newCtx.update(resp.getInstructions());
      
      String spoken = (String) newCtx.getOrElse("$utt:/action.spoken", "");
      String spokenResponse = resp.getSpokenResponse();
      if (!Strings.isNullOrEmpty(spokenResponse)) {
        spoken += "\n" + spokenResponse;
      }
      newCtx = newCtx.put("$utt:/action.spoken", spoken);
      
      ActionProcessor ap = bot.pickActionProcessor(resp);
      for (Action a : resp.getActions()) {
        ActionResponse ar = ap.process(a, newCtx, bot, u);
        newCtx = ar.getContext();
        String newSpoken = ar.getSpokenResponse();
        if (newSpoken != null) {
          // append spoken result of Action to spoken result
          spoken = (String) newCtx.get("$utt:/action.spoken");
          spoken += "\n" + newSpoken;
          newCtx = newCtx.put("$utt:/action.spoken", spoken);
        }
      }
      context = newCtx;
    }
    if (context.get("$utt:/intent?validated") != null &&
        (boolean) context.get("$utt:/intent?validated")) {
      List<String> objectSlots = bot.getIntent(newIntent).getObjectSlots();
      List<Instruction> instr = map(objectSlots,
          s -> new RemoveSlot(InstructionContext.UTTERANCE, "/objects" + s));
      context = context.update(instr);
    }
    return new ActionResponse(extractSpokenResponse(context), context);
  }
}
