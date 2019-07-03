package org.mholford.chatlantis.intent;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.mholford.chatlantis.Utils;
import org.mholford.chatlantis.action.Action;
import org.mholford.chatlantis.validation.Constraint;
import org.mholford.chatlantis.validation.ConstraintConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Encapsulates user configuration of an Intent.  Typically this class will be populated
 * by deserialization from the chatlantis.json config file.  The init method (called during
 * Chatlantis initialization) is responsible for creating Constraint objects and Action objects.
 */
public class IntentConfig implements Utils {
  private String name;
  private List<String> successTemplate;
  private String initialResponsePrompt;
  private String denyIntent;
  private List<String> objectSlots;
  
  @JsonProperty("successActions")
  private List<String> successActionNames;
  
  private boolean confirmOnValid;
  
  @JsonProperty("constraints")
  private List<ConstraintConfig> constraintConfigs;
  
  private final IntentFactory ifac = IntentFactory.get();
  
  /**
   * Initializes this Intent by attaching Constraint and Action objects.  The Actions were built
   * previously during initialization and so are passed in as a parameter.
   * @param actionMap Map of Actions by name
   * @return Fully initialized Intent
   * @throws IOException If there was problem reading config
   * @throws ReflectiveOperationException If there was problem creating a helper class
   */
  public Intent init(Map<String, Action> actionMap) throws IOException, ReflectiveOperationException {
    List<Constraint> constraints = new ArrayList<>();
    for (ConstraintConfig cc: constraintConfigs) {
      constraints.add(cc.init());
    }
    List<Action> successActions = map(successActionNames, a -> actionMap.get(a));
    return ifac.createIntent(name, constraints, successTemplate, successActions, confirmOnValid,
        initialResponsePrompt, denyIntent, objectSlots);
  }
  
  /**
   * Gets the name of the Intent
   * @return Name of Intent
   */
  public String getName() {
    return name;
  }
  
  /**
   * Sets the name of the Intent to the specified value
   * @param name Name of Intent
   */
  public void setName(String name) {
    this.name = name;
  }
  
  /**
   * Gets the template to display when Intent has been reached
   * @return Template string
   */
  public List<String> getSuccessTemplate() {
    return successTemplate;
  }
  
  /**
   * Sets the success template to the specified value
   * @param successTemplate Template string
   */
  public void setSuccessTemplate(List<String> successTemplate) {
    this.successTemplate = successTemplate;
  }
  
  /**
   * Gets the names of the Actions to be performed on reaching the Intent
   * @return List of action names
   */
  public List<String> getSuccessActionNames() {
    return successActionNames;
  }
  
  /**
   * Sets the names of the Actions to be performed on reaching the Intent to the specified values
   * @param successActionNames Names of actions
   */
  public void setSuccessActionNames(List<String> successActionNames) {
    this.successActionNames = successActionNames;
  }
  
  /**
   * Get whether user confirmation is required once the Intent has been reached
   * @return Whether confirmation is needed
   */
  public boolean isConfirmOnValid() {
    return confirmOnValid;
  }
  
  /**
   * Sets whether user confirmation is required once the Intent has been reached to the
   * specified value
   * @param confirmOnValid Whether confirmation is needed
   */
  public void setConfirmOnValid(boolean confirmOnValid) {
    this.confirmOnValid = confirmOnValid;
  }
  
  /**
   * Gets the list of Constraint configurations associated with this Intent.
   * @return List of Constraint configuration objects
   */
  public List<ConstraintConfig> getConstraintConfigs() {
    return constraintConfigs;
  }
  
  /**
   * Sets the list of Constraint configurations associated with this Intent to the specified
   * values
   * @param constraintConfigs List of Constraint configuration objects
   */
  public void setConstraintConfigs(List<ConstraintConfig> constraintConfigs) {
    this.constraintConfigs = constraintConfigs;
  }
  
  /**
   * Sets the prompt that should be spoken when Intent switches to this one.  By default,
   * nothing will be said.
   * @return Spoken prompt
   */
  public String getInitialResponsePrompt() {
    return initialResponsePrompt;
  }
  
  /**
   * Sets the prompt that should be spoken when Intent swithch to this one to the specified value.
   * @param initialResponsePrompt Spoken prompt
   */
  public void setInitialResponsePrompt(String initialResponsePrompt) {
    this.initialResponsePrompt = initialResponsePrompt;
  }
  
  /**
   * Gets the name of the Intent that should be enabled if the user declines confirmation
   * @return Name of Intent
   */
  public String getDenyIntent() {
    return denyIntent;
  }
  
  /**
   * Sets the name of the Intent that should be enabled if the user declines confirmation to the
   * specified value
   * @param denyIntent Name of Intent
   */
  public void setDenyIntent(String denyIntent) {
    this.denyIntent = denyIntent;
  }
  
  /**
   * Gets the paths in Utterance Context's /object map dedicated to this Intent
   * @return List of paths
   */
  public List<String> getObjectSlots() {
    return objectSlots;
  }
  
  /**
   * Sets the paths in Utterance Context's /object map dedicated to this Intent to the specified
   * @param objectSlots List of paths
   */
  public void setObjectSlots(List<String> objectSlots) {
    this.objectSlots = objectSlots;
  }
}
