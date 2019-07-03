package org.mholford.chatlantis.intent;

import org.mholford.chatlantis.Utils;
import org.mholford.chatlantis.action.Action;
import org.mholford.chatlantis.context.FullContext;
import org.mholford.chatlantis.validation.Constraint;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a "goal" or "target" in a Conversation.  During processing, Chatlantis attempts
 * to map each Utterance to an Intent and then to see how well that Intent has been achieved.
 * Intents contain the following properties:<ul>
 *   <li>name - A name</li>
 *   <li>constraints - A list of Constraint objects which must be validated before an Intent is
 *   successfully reached</li>
 *   <li><successTemplate - Spoken response if Intent is reached</li>
 *   <li>successActions - Actions to be performed if the Intent is reached</li>
 *   <li>requiredConfirmation - Tells the bot to confirm with user after Intent is reached</li>
 *   <li>initialResponsePrompt - What the bot should say (if anything) when the Intent changes
 *   to this one.</li>
 *   <li>denyIntent - If the user declines to confirm, set the Intent to this one</li>
 *   <li>objectSlots - Paths within the objects map in Utterance Context that are owned
 *   by this Intent.  This allows us to clean up after the Intent has been met</li>
 * </ul>
 * Intents are usually configured via a stanza in the chatlantis.json configuration file.
 * Intents are immutable and should be instantiated using the IntentFactory.  This is done
 * behind the scenes by Chatlantis as it initializes Bots from the chatlantis.json file.
 */
public class Intent implements Utils {
  private final String name;
  private final List<Constraint> constraints;
  private final List<String> successTemplate;
  private final List<Action> successActions;
  private final boolean requiresConfirmation;
  private final String initialResponsePrompt;
  private final String denyIntent;
  private final List<String> objectSlots;
  
  Intent(String name, List<Constraint> constraints, List<String> successTemplate,
         List<Action> successActions, boolean requiresConfirmation, String initialResponsePrompt,
         String denyIntent, List<String> objectSlots) {
    this.name = name;
    this.constraints = constraints;
    this.successTemplate = successTemplate;
    this.successActions = successActions;
    this.requiresConfirmation = requiresConfirmation;
    this.initialResponsePrompt = initialResponsePrompt;
    this.denyIntent = denyIntent;
    this.objectSlots = objectSlots;
  }
  
  /**
   * Gets the prompt (if any) the bot should say when it switches to this Intent
   * @return Spoken prompt
   */
  public String getInitialResponsePrompt() {
    return initialResponsePrompt;
  }
  
  /**
   * Gets the Intent that should be enabled if the user declines to confirm this Intent
   * @return Name of Intent
   */
  public String getDenyIntent() {
    return denyIntent;
  }
  
  /**
   * Gets the name of this Intent
   * @return Name of Intent
   */
  public String getName() {
    return name;
  }
  
  /**
   * Gets all Constraint objects which must be validated before this Intent is realized
   * @return List of Constraints
   */
  public List<Constraint> getConstraints() {
    return constraints;
  }
  
  /**
   * Gets all Constraints for this Intent that involve the specified slot.
   * @param slot Slot in Context (in Context DSL path form)
   * @return List of Constraints
   */
  public List<Constraint> getConstraintsForSlot(String slot) {
    return filter(constraints, c -> c.getPath().equals(slot));
  }
  
  /**
   * Returns the success template with values filled in from the FullContext snapshot.
   * The template should include Context DSL paths surrounded by '<' and '>'.  As this resolves
   * against the FullContext, the Context DSL paths must include context labels.
   * @param ctx Context snapshot to resolve the template against
   * @return Processed template
   */
  public String processSuccessTemplate(FullContext ctx) {
    // Join together the template pieces
    String template = String.join("", successTemplate);
    // Get placeholders in successTemplate
    // Find placeholders in ctx and replace them
    List<String> replacements = new ArrayList<>();
    List<String> tokens = tokenize(template, TokenizerMode.WHITESPACE);
    for (String t : tokens) {
      if (t.startsWith("<") && t.contains(">")) {
        int endIdx = t.indexOf('>');
        String lu = t.substring(1, endIdx);
        String post = t.substring(endIdx + 1);
        String lookup = (String) ctx.get(lu);
        replacements.add(lookup + post);
      } else {
        replacements.add(t);
      }
    }
    return String.join(" ", replacements);
  }
  
  /**
   * Gets the list of Actions to be performed once the Intent has been reached.
   * @return List of Actions
   */
  public List<Action> getSuccessActions() {
    return successActions;
  }
  
  /**
   * Answers whether the Intent should be confirmed by the user after it has been reached.
   * @return Whether the Intent requires confirmation
   */
  public boolean isRequiresConfirmation() {
    return requiresConfirmation;
  }
  
  /**
   * Joins the pieces of the success template together into a single string.  The template
   * is broken up into chunks for readability within the json config file.  (JSON does not
   * support word wrapping).
   * @return Template string
   */
  public String getSuccessTemplateString() {
    return String.join("", successTemplate);
  }
  
  /**
   * Gets the paths in /objects map in Utterance context that are devoted to this Intent.  These
   * paths will be deleted when this Intent has been met.
   * @return List of paths
   */
  public List<String> getObjectSlots() {
    return objectSlots;
  }
}
