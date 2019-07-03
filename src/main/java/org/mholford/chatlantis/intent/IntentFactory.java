package org.mholford.chatlantis.intent;

import org.mholford.chatlantis.action.Action;
import org.mholford.chatlantis.validation.Constraint;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton factory class responsible for creating Intent instances.  Typically, this is
 * called from within Chatlantis initialization code.
 */
public class IntentFactory {
  private static IntentFactory INSTANCE;
  
  private IntentFactory() {
  
  }
  
  /**
   * Gets the singleton instance, creating it if needed.
   * @return Singleton factory instance
   */
  public static IntentFactory get() {
    if (INSTANCE == null) {
      INSTANCE = new IntentFactory();
    }
    return INSTANCE;
  }
  
  private List<Constraint> empty() {
    return new ArrayList<>();
  }
  
  /**
   * Creates a new Intent composed of the helper objects and parameters specified.  This is
   * typically called from within Chatlantis initialization code.
   * @param name Name of the intent
   * @param constraints List of Constraint objects
   * @param successTemplate Template to return if Intent is reached
   * @param successActions Actions to be performed if Intent is reached
   * @param requiresConfirmation Whether user confirmation is needed after Intent is reached
   * @param initialResponsePrompt Initial spoken prompt when switching to this Intent
   * @param denyIntent Intent to switch to if customer declines confirmation
   * @param objectSlots Slots in $utt:/objects dedicated to this Intent
   * @return Fully initialized Intent
   */
  public Intent createIntent(String name, List<Constraint> constraints, List<String> successTemplate,
                             List<Action> successActions, boolean requiresConfirmation,
                             String initialResponsePrompt, String denyIntent, List<String>
                             objectSlots) {
    return new Intent(name, constraints, successTemplate, successActions, requiresConfirmation,
        initialResponsePrompt, denyIntent, objectSlots);
  }
}
