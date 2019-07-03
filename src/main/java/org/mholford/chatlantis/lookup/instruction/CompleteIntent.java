package org.mholford.chatlantis.lookup.instruction;

import com.google.common.base.Strings;
import org.mholford.chatlantis.Utils;
import org.mholford.chatlantis.context.FullContext;
import org.pcollections.PSequence;

import static org.mholford.chatlantis.lookup.instruction.InstructionContext.UTTERANCE;

/**
 * This Instruction serves to handle the case when an Intent has completed/validated.
 * A stack of previousIntents is maintained in Conversation context.  When Intent
 * switches from Intent A to Intent B, Intent A is put on the stack of previousIntents.
 * Then once Intent B completes, Intent A is popped off the stack and becomes the
 * current Intent again.
 */
public class CompleteIntent implements Instruction, Utils {
  private final InstructionContext instructionContext;
  
  public CompleteIntent(InstructionContext instructionContext) {
    this.instructionContext = instructionContext;
  }
  
  public CompleteIntent() {
    this(UTTERANCE);
  }
  
  @Override
  public String output() {
    // TODO: Why this works and not just returning null?
    return fmt("-! $%s:%s", instructionContext.getAbbrev(), "/wtf"/*INTENT*/);
    //return null;
  }
  
  @Override
  public InstructionContext getInstructionContext() {
    return null;
  }
  
  @Override
  public FullContext preExec(FullContext fc) {
    // Get last element of previousIntents and designate as next Intent
    PSequence prevIntents = (PSequence) fc.get("$conv:|previousIntents");
    if (prevIntents != null && prevIntents.size() > 0) {
      String nextIntent = (String) prevIntents.get(prevIntents.size() - 1);
      if (!Strings.isNullOrEmpty(nextIntent)) {
        fc = fc.put("$utt:.nextIntent", nextIntent);
      }
    }
    return fc;
  }
  
  @Override
  public FullContext postExec(FullContext fc) {
    String nextIntent = (String) fc.get("$utt:.nextIntent");
    if (!Strings.isNullOrEmpty(nextIntent)) {
      fc = fc.put("$utt:/intent.name", nextIntent);
      PSequence prevIntents = (PSequence) fc.get("$conv:|previousIntents");
      if (prevIntents != null) {
        // Pop off the last intent
        int l = prevIntents.size();
        prevIntents = prevIntents.minus(l - 1);
        fc = fc.remove("$conv:|previousIntents");
        fc = fc.put("$conv:|previousIntents", prevIntents);
      }
    }
    
    return fc;
  }
}
