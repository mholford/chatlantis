package org.mholford.chatlantis.prompt;

import org.mholford.chatlantis.context.FullContext;

import java.util.Map;

/**
 * The Prompt mechanism allows for simple questions and answers
 * to be processed without creating a separate Intent.  Typical
 * use cases include: <ul>
 *   <li>Adding/modifying slots to make an Intent valid</li>
 *   <li>Handling user confirmation of a valid Intent</li>
 * </ul>
 * PromptHandlers are usually configured in the chatlantis.json
 * file and initialized at startup of Chatlantis from configs.
 */
public interface PromptHandler {
  /**
   * Handle prompt based upon current FullContext snapshot.  Returns
   * a new snapshot incorporating the changes.  This is called internally
   * by Chatlantis' default Workflow after Intent matching and before
   * Intent resolution.
   * @param ctx Context snapshot
   * @return Context snapshot incorporating response to prompt
   */
  FullContext handlePrompt(FullContext ctx);
  
  /**
   * Initializes a Prompt handler from the map of properties.  By
   * default, this method is a no-op.
   * @param props Property map
   */
  default void init(Map<String, String> props) {}
}
