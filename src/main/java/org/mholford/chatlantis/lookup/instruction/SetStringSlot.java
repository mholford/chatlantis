package org.mholford.chatlantis.lookup.instruction;

import org.mholford.chatlantis.lookup.Marker;

/**
 * Specializes SetSlot to assign String values.
 */
public class SetStringSlot extends SetSlot<String> {
  
  /**
   * Sets the specified slot to the specified value in the specified Context, optionally
   * prefetching value from Context
   * @param instructionContext Which Context
   * @param slotName Which slot
   * @param slotValue What to set slot to
   * @param prefetchValue Whether to prefetch value from Context
   */
  public SetStringSlot(InstructionContext instructionContext, String slotName, String slotValue,
                       boolean prefetchValue) {
    super(instructionContext, slotName, slotValue, prefetchValue);
  }
  
  /**
   * Sets the specified slot to the specified value in the specified Context without prefetching
   * the value from Context
   * @param instructionContext Which Context
   * @param slotName Which slot
   * @param slotValue What to set slot to
   */
  public SetStringSlot(InstructionContext instructionContext, String slotName, String slotValue) {
    super(instructionContext, slotName, slotValue);
  }
  
  /**
   * Sets the specified slot to the specified value in Utterance Context without prefetching the
   * value from Context
   * @param slotName Which slot
   * @param slotValue What to set slot to
   */
  public SetStringSlot(String slotName, String slotValue) {
    super(slotName, slotValue);
  }
  
  /**
   * Sets the specified slot to the value of the specified Marker in the specified Context
   * @param instructionContext Which Context
   * @param slotName Which slot
   * @param slotValueMarker Which Marker
   */
  public SetStringSlot(InstructionContext instructionContext, String slotName,
                       Marker slotValueMarker) {
    super(instructionContext, slotName, slotValueMarker);
  }
  
  /**
   * Sets the specified slot to the value of the specified Marker in Utterance Context
   * @param slotName Which slot
   * @param slotValueMarker Which Marker
   */
  public SetStringSlot(String slotName, Marker slotValueMarker) {
    super(slotName, slotValueMarker);
  }
}
