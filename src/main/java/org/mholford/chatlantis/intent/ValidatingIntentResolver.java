package org.mholford.chatlantis.intent;

import org.mholford.chatlantis.Utils;
import org.mholford.chatlantis.action.Action;
import org.mholford.chatlantis.bot.Bot;
import org.mholford.chatlantis.context.ContextConstants;
import org.mholford.chatlantis.context.FullContext;
import org.mholford.chatlantis.lookup.instruction.*;
import org.mholford.chatlantis.validation.Constraint;
import org.mholford.chatlantis.validation.Violation;
import org.mholford.chatlantis.workflow.Response;
import org.mholford.chatlantis.workflow.ResponseFactory;

import java.io.IOException;
import java.util.*;

/**
 * Implementation of IntentResolver that will attempt to validate the current Intent
 * by seeing how many of the Constraints associated with it pass.  A score is assigned
 * based on the value of numConstraintsPassed / numConstraints.
 * <p>
 *   In the event that all Constraints pass, this class will retrieve the success response
 * for the Intent.  If the Intent requires user confirmation, the Context is supplied
 * with Prompt information to facilitate this.  ValidatingIntentResolver handles the logic
 * of whether the user confirms the validated Intent.  If the user declines, Intent is
 * switched to the denyIntent parameter of the Intent.  If the user affirms, this class
 * retrieves Actions associated with the Intent and bundles them with the template in a
 * Response object.  (This last step also occurs if the Intent is not set to require
 * confirmation.
 * </p>
 * <p>
 *   In the event that some Constraints fail, ValidatingIntentResolver will handle prompting
 *   the user to supply one of the missing values.  (The actual missing value is indeterminate
 *   because violations are returned as a Set).
 * </p>
 */
public class ValidatingIntentResolver implements IntentResolver, ContextConstants, Utils {
  
  @Override
  public Response resolve(Intent intent, Bot bot, FullContext ctx) {
    Set<Violation> violations = new HashSet<>();
    intent.getConstraints().forEach(
        c -> violations.addAll(validate(c, ctx)));
    double score = (double) (intent.getConstraints().size() - violations.size())
        / intent.getConstraints().size();
    if (violations.size() > 0) {
      return constraintViolationResponse(intent, violations, ctx, score);
    } else {
      return successResponse(intent, bot, ctx);
    }
  }
  
  private Response constraintViolationResponse(Intent intent, Set<Violation> violations,
                                               FullContext ctx, double score) {
    Violation v = violations.iterator().next();
    String msg = v.getPrompt();
    List<Instruction> instructions = new ArrayList<>();
    instructions.addAll(listOf(
        new SetStringSlot(InstructionContext.CONVERSATION, "/prompt.question", msg),
        new SetStringSlot(InstructionContext.CONVERSATION, "/prompt.slot", v.getSlot()),
        new SetStringSlot(InstructionContext.CONVERSATION, "/prompt.intent", intent.getName()),
        new SetBooleanSlot(InstructionContext.UTTERANCE, "/intent?validated", false)));
    return ResponseFactory.get().createResponse(msg, listOf(), instructions, ctx, score);
  }
  
  private Response successResponse(Intent intent, Bot bot,  FullContext ctx) {
    StringBuilder intentResponse = new StringBuilder(intent.processSuccessTemplate(ctx));
    List<Instruction> instructions = new ArrayList<>();
    List<Action> actions = new ArrayList<>();
    if (intent.isRequiresConfirmation()) {
      Boolean isConfirmed = (Boolean) ctx.get("$utt:/intent?confirmed");
      if (isConfirmed == null) {
        instructions.addAll(listOf(
            new SetStringSlot(InstructionContext.CONVERSATION, "/prompt.question", "Is that okay?"),
            new SetStringSlot(InstructionContext.CONVERSATION, "/prompt.slot", "$utt:/intent?confirmed"),
            new SetStringSlot(InstructionContext.CONVERSATION, "/prompt?boolean", "true"),
            new SetStringSlot(InstructionContext.CONVERSATION, "/prompt.intent", intent.getName()),
            new SetBooleanSlot(InstructionContext.UTTERANCE, "/intent?validated", false)));
        intentResponse.append("\nIs that okay?");
      } else if (!isConfirmed) { /* explicitly denied confirmation */
        intentResponse.setLength(0);
        String denyIntentName = intent.getDenyIntent();
        Intent denyIntent = bot.getIntent(denyIntentName);
        intentResponse.append(denyIntent.getInitialResponsePrompt());
        instructions.addAll(listOf(
            new SetIntent(InstructionContext.UTTERANCE, denyIntent.getName()),
            new SetBooleanSlot(InstructionContext.UTTERANCE, "/intent?validated", false)));
      } else { /*confirmed*/
        intentResponse.setLength(0);
        actions = intent.getSuccessActions();
        // Remove all slots dedicated to the outgoing intent
        //instructions.addAll(map(intent.getObjectSlots(),
        //    s -> new RemoveSlot(UTTERANCE, "/objects" + s)));
        instructions.addAll(listOf(new CompleteIntent(),
            new SetBooleanSlot(InstructionContext.UTTERANCE, "/intent?validated", true),
            new RemoveSlot(InstructionContext.CONVERSATION, "/prompt")));
      }
    } else { /* Not requires confirmation */
      actions = intent.getSuccessActions();
      //// Remove all slots dedicated to the outgoing intent
      //instructions.addAll(map(intent.getObjectSlots(),
      //    s -> new RemoveSlot(UTTERANCE, "/objects" + s)));
      instructions.addAll(listOf(new RemoveSlot(InstructionContext.CONVERSATION, "/prompt"),
          new SetBooleanSlot(InstructionContext.UTTERANCE, "/intent?validated", true)));
    }
    return ResponseFactory.get().createResponse(intentResponse.toString(), actions, instructions,
        ctx, 1d);
  }
  
  private Set<Violation> validate(Constraint c, FullContext ctx) {
    Object value = ctx.get(c.getPath());
    return c.getValidator().validate(value, ctx, c.getPrompt(), c.getPath(), c.getErrorMessage());
  }
}
