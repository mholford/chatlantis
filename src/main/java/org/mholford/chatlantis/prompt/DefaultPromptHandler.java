package org.mholford.chatlantis.prompt;

import org.mholford.chatlantis.Utils;
import org.mholford.chatlantis.context.FullContext;
import org.mholford.chatlantis.context.SubExpression;
import org.mholford.chatlantis.lookup.instruction.InstructionContext;
import org.mholford.chatlantis.lookup.instruction.SetBooleanSlot;
import org.mholford.chatlantis.lookup.instruction.SetStringSlot;

import java.util.List;
import java.util.Map;

/**
 * Implementation of PromptHandler to handle most, if not all, use cases.  Checks for
 * value of "prompt.value" slot in Utterance context and fills it into the slot specified
 * in the "prompt.slot" slot in Utterance context.  In the event the "prompt?boolean" slot
 * is true (e.g. for the case of confirming a valid Intent), will attempt to "booleanize"
 * the value.  Words like "yes" and "yup" will map to true while things like "no" and
 * "nope" map to false.
 */
public class DefaultPromptHandler implements PromptHandler, Utils {
  @Override
  public FullContext handlePrompt(FullContext ctx) {
    if (ctx.get("$utt:/prompt.value") != null) {
      Object promptValue = ctx.get("$utt:/prompt.value");
      
      boolean isBooleanPrompt = (boolean) ctx.getOrElse("$utt:/prompt?boolean", false);
      if (isBooleanPrompt) {
        promptValue = booleanize(promptValue);
      }
      String promptSlot = (String) ctx.get("$utt:/prompt.slot");
      if (promptSlot != null && promptValue != null) {
        SubExpression slotSubEx = getSubEx(promptSlot);
        InstructionContext ic = slotSubEx.getInstructionContext();
        promptSlot = slotSubEx.getExpr();
        if (isBooleanPrompt) {
          ctx = ctx.update(listOf(new SetBooleanSlot(ic, promptSlot, (Boolean) promptValue)));
        } else {
          ctx = ctx.update(listOf(new SetStringSlot(ic, promptSlot, (String) promptValue)));
        }
      }
    }
    return ctx;
  }
  
  private Boolean booleanize(Object o) {
    if (!(o instanceof String)) {
      return null;
    }
    String s = (String) o;
    List<String> truthWords = listOf("yes", "sure", "ok", "true", "y", "okay", "yup", "yeah", "ya");
    List<String> falseWords = listOf("false", "no", "nay", "not", "never", "no way", "negatory");
    if (truthWords.contains(s)) {
      return true;
    } else if (falseWords.contains(s)) {
      return false;
    } else {
      return null;
    }
  }
}
