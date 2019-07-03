package org.mholford.chatlantis.lookup.instruction;

import org.mholford.chatlantis.Utils;
import org.mholford.chatlantis.context.FullContext;
import org.pcollections.PSequence;
import org.pcollections.TreePVector;

import static org.mholford.chatlantis.lookup.instruction.InstructionConstants.INTENT;

/**
 * Instruction to change current Intent.  Will also put the previous Intent
 * in a stack of nextIntents maintained on the Conversation Context.  Note that
 * the CompleteIntent Instruction also makes use of this stack.
 */
public class SetIntent implements Instruction, Utils {
  private final String intent;
  private final InstructionContext instructionContext;
  
  /**
   * Constructs a SetIntent Instruction using the specified values
   * @param instructionContext Context
   * @param intent Name of Intent
   */
  public SetIntent(InstructionContext instructionContext, String intent) {
    this.intent = intent;
    this.instructionContext = instructionContext;
  }
  
  /**
   * Constructs a SetIntent Instruction using the specified intent in the
   * Utterance Context
   * @param intent Name of Intent
   */
  public SetIntent(String intent) {
    this(InstructionContext.UTTERANCE, intent);
  }
  
  @Override
  public String output() {
    return fmt("$%s:%s -> %s", instructionContext.getAbbrev(), INTENT, intent);
  }
  
  @Override
  public InstructionContext getInstructionContext() {
    return instructionContext;
  }
  
  @Override
  public FullContext preExec(FullContext fc) {
    String currIntent = (String) fc.get("$utt:/intent.name");
    if (currIntent != null) {
      fc = fc.update(listOf(new SetStringSlot(InstructionContext.UTTERANCE, ".previousIntent", currIntent)));
    }
    return fc;
  }
  
  @Override
  public FullContext postExec(FullContext fc) {
    String prevIntent = (String) fc.get("$utt:.previousIntent");
    if (prevIntent != null) {
      PSequence prevIntents = (PSequence) fc.get("$conv:|previousIntents");
      if (prevIntents != null) {
        prevIntents = prevIntents.plus(prevIntent);
      } else {
        prevIntents = TreePVector.from(listOf(prevIntent));
      }
      fc = fc.remove("$conv:|previousIntents");
      fc = fc.put("$conv:|previousIntents", prevIntents);
    }
    return fc;
  }
}
