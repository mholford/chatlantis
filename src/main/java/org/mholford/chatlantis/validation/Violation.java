package org.mholford.chatlantis.validation;

/**
 * Represents a Violation on a Constraint.  It is composed of:<ul>
 *   <li>prompt - Message to display when prompting to fix violation</li>
 *   <li>slot - Context DSL path to slot violation occurred on</li>
 *   <li>errorMessage - Detail on why the violation occurred</li>
 * </ul>
 * The Violation class is effectively immutable.
 */
public class Violation {
  private final String prompt;
  private final String slot;
  private final String errorMessage;
  
  /**
   * Creates a new Violation with the specified prompt, slot and error detail
   * @param message Prompt message
   * @param slot Slot in violation
   * @param errorMessage Detailed error
   */
  public Violation(String message, String slot, String errorMessage) {
    this.prompt = message;
    this.slot = slot;
    this.errorMessage = errorMessage;
  }
  
  /**
   * Gets the message to display when prompting to fix a violation
   * @return Prompt message
   */
  public String getPrompt() {
    return prompt;
  }
  
  /**
   * Gets the Context DSL path of the slot on which the violation occurred
   * @return Slot path
   */
  public String getSlot() {
    return slot;
  }
  
  /**
   * Gets the detailed message on why the violation occurred.
   * @return Error message
   */
  public String getErrorMessage() {
    return errorMessage;
  }
}
