package org.mholford.chatlantis.lookup.instruction;

import org.mholford.chatlantis.Utils;

import static org.mholford.chatlantis.lookup.instruction.InstructionContext.UTTERANCE;

/**
 * Removes the specified slot from the specified Context.
 * Slot name should be a Context DSL path.
 */
public class RemoveSlot implements Instruction, Utils {
  private final InstructionContext instructionContext;
  private final String slotName;
  
  /**
   * Creates a new RemoveSlot instruction with the specified parameters.
   * @param instructionContext Which context
   * @param slotName Which slot (Context DSL path)
   */
  public RemoveSlot(InstructionContext instructionContext, String slotName) {
    this.instructionContext = instructionContext;
    this.slotName = slotName;
  }
  
  /**
   * Creates a new RemoveSlot instruction for the specified slot in the
   * Utterance context
   * @param slotName Which slot (Context DSL path)
   */
  public RemoveSlot(String slotName) {
    this(UTTERANCE, slotName);
  }
  
  @Override
  public String output() {
    return fmt("-! $%s:%s", instructionContext.getAbbrev(), slotName);
  }
  
  @Override
  public InstructionContext getInstructionContext() {
    return instructionContext;
  }
}
