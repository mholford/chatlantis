package org.mholford.chatlantis.validation;

import java.io.IOException;
import java.util.Map;

/**
 * Encapsulation of user configuration of a Constraint.  This is usually deserialized
 * from chatlantis.json configuration.  When initialized (by calling init()), will
 * create an instance of the specified Validator class using the specified parameters.
 * Initialization occurs behind the scenes during initialization of Chatlantis from
 * configuration files.
 */
public class ConstraintConfig {
  private String slot;
  private String prompt;
  private String errorMessage;
  private String validatorClass;
  private Map<String, String> validatorParams;
  
  /**
   * Initializes the Constrain by creating an instance of the Validator class specified
   * @return Fully initialized Constraint
   * @throws ReflectiveOperationException If the Validator class could not be instantiated
   * @throws IOException If something else went wrong
   */
  public Constraint init() throws ReflectiveOperationException, IOException {
    Class<Validator> valClz = (Class<Validator>) Class.forName(validatorClass);
    Validator validator = valClz.newInstance();
    validator.init(validatorParams);
    return new Constraint(slot, validator, prompt, errorMessage);
  }
  
  /**
   * Gets the Context DSL path for the slot that is being constrained
   * @return slot path
   */
  public String getSlot() {
    return slot;
  }
  
  /**
   * Sets the Context DSL path for the slot this is being constrained to the specified value
   * @param slot slot path
   */
  public void setSlot(String slot) {
    this.slot = slot;
  }
  
  /**
   * Gets what to display when prompting for an unmet constraint
   * @return Prompt message
   */
  public String getPrompt() {
    return prompt;
  }
  
  /**
   * Sets what to display when prompting for an unmet constraint to the specified value
   * @param prompt Prompt message
   */
  public void setPrompt(String prompt) {
    this.prompt = prompt;
  }
  
  /**
   * Gets the fully qualified class name of the Validator class
   * @return Class name
   */
  public String getValidatorClass() {
    return validatorClass;
  }
  
  /**
   * Sets the fully qualified class name of the Validator to the specified value
   * @param validatorClass Class name
   */
  public void setValidatorClass(String validatorClass) {
    this.validatorClass = validatorClass;
  }
  
  /**
   * Gets the map of parameters used to initialize the Validator
   * @return Map of parameters
   */
  public Map<String, String> getValidatorParams() {
    return validatorParams;
  }
  
  /**
   * Sets the map of parameters used to initialized the Validator to the specified value
   * @param validatorParams Map of parameters
   */
  public void setValidatorParams(Map<String, String> validatorParams) {
    this.validatorParams = validatorParams;
  }
  
  /**
   * Gets the detailed error message to show why the constraint is unmet
   * @return Error message
   */
  public String getErrorMessage() {
    return errorMessage;
  }
  
  /**
   * Sets the detailed error message to show why the constrain is unmet to the specified value
   * @param errorMessage Error message
   */
  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }
}
