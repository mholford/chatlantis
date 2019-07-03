package org.mholford.chatlantis.lookup.instruction;

/**
 * Specializes SetSlot to assign Numeric values.
 */
public class SetNumericSlot extends SetSlot<Number> {
  
  /**
   * Sets the specified slot to the specified Numeric value in the specified Context,
   * optionally prefetching values from Context.
   * @param instructionContext Which Context
   * @param slotName Which slot
   * @param slotValue Value to set slot to
   * @param prefetchValue Whether to prefetch value from Context
   */
  public SetNumericSlot(InstructionContext instructionContext, String slotName, Number slotValue,
                        boolean prefetchValue) {
    super(instructionContext, slotName, slotValue, prefetchValue);
  }
}
