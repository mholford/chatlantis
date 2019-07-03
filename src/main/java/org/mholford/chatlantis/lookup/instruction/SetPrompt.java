package org.mholford.chatlantis.lookup.instruction;

import org.mholford.chatlantis.lookup.Marker;

/**
 * Specializes SetStringSlot to set the value of the PROMPT slot
 */
public class SetPrompt extends SetStringSlot {
  
  /**
   * Sets the PROMPT slot to the specified value in the specified Context
   * @param instructionContext Which Context
   * @param promptValue Value to set prompt to
   */
  public SetPrompt(InstructionContext instructionContext, String promptValue) {
    super(instructionContext, InstructionConstants.PROMPT, promptValue);
  }
  
  /**
   * Set the PROMPT slot to the value of the specified Marker in the specified Context
   * @param instructionContext Which Context
   * @param marker Which marker
   */
  public SetPrompt(InstructionContext instructionContext, Marker marker) {
    super(instructionContext, InstructionConstants.PROMPT, marker);
  }
  
  /**
   * Sets the PROMPT slot to the value of the specified Marker in Utterance Context
   * @param marker Which Marker
   */
  public SetPrompt(Marker marker) {
    this(InstructionContext.UTTERANCE, marker);
  }
}
