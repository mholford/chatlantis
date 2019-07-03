package org.mholford.chatlantis.lookup.instruction;

/**
 * Specializes SetSlot instruction to set a Boolean value.
 */
public class SetBooleanSlot extends SetSlot<Boolean> {
  
  /**
   * Set the specified slot to the specified boolean value in the specified Context.
   * @param instructionContext Which Context
   * @param slotName Which slot
   * @param slotValue Value to set slot to
   */
  public SetBooleanSlot(InstructionContext instructionContext, String slotName, Boolean slotValue) {
    super(instructionContext, slotName, slotValue);
  }
}
