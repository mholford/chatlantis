package org.mholford.chatlantis.validation;

/**
 * Representation of a Constraint on an Intent.  It is composed of the following:<ul>
 *   <li>path - Context DSL path the the slot that is constrained</li>
 *   <li>validator - Instance of the class which will validate this property</li>
 *   <li>prompt - What to display when prompting for the unmet Constraint</li>
 *   <li>errorMessage - Detail to display on why the constraint failed</li>
 * </ul>
 * Constraints are effectively immutable.
 */
public class Constraint {
  final String path;
  final Validator validator;
  final String prompt;
  final String errorMessage;
  
  /**
   * Creates a new Constraint from the specified parameters
   * @param path Context DSL path to the slot that is constrained
   * @param validator What performs the validation
   * @param prompt Prompt to show when asking for unmet Constraint
   * @param errorMessage Detail on why constraint is invalid
   */
  public Constraint(String path, Validator validator, String prompt, String errorMessage) {
    this.path = path;
    this.validator = validator;
    this.prompt = prompt;
    this.errorMessage = errorMessage;
  }
  
  /**
   * Gets the context DSL path to the slot that is constrained
   * @return Context DSL path
   */
  public String getPath() {
    return path;
  }
  
  /**
   * Gets the validator that will validate the slot
   * @return Validator instance
   */
  public Validator getValidator() {
    return validator;
  }
  
  /**
   * Gets the prompt to show when asking for an unmet Constraint
   * @return Prompt to show
   */
  public String getPrompt() {
    return prompt;
  }
  
  /**
   * Gets the detail to display as to why the Constraint is unmet
   * @return
   */
  public String getErrorMessage() {
    return errorMessage;
  }
}
