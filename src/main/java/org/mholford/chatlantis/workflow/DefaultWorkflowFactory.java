package org.mholford.chatlantis.workflow;

import org.mholford.chatlantis.intent.IntentMatcher;
import org.mholford.chatlantis.intent.IntentResolver;
import org.mholford.chatlantis.prompt.PromptHandler;

import java.util.List;

/**
 * Singleton factory class to create new instances of DefaultWorkflow.  This is usually
 * called during initialization of Chatlantis from the chatlantis.json config file.
 */
public class DefaultWorkflowFactory {
  private static DefaultWorkflowFactory INSTANCE;
  
  private DefaultWorkflowFactory() {}
  
  /**
   * Gets the singleton instance of the factory, initializing it if needed.
   * @return Singleton factory
   */
  public static DefaultWorkflowFactory get() {
    if (INSTANCE == null) {
      INSTANCE = new DefaultWorkflowFactory();
    }
    return INSTANCE;
  }
  
  /**
   * Creates a new DefaultWorkflow with the specified name and helper classes.
   * @param name Name of the workflow
   * @param processors Utterance processors used by the workflow
   * @param matchers Intent matchers used by the workflow
   * @param resolvers Intent resolvers used by the workflow
   * @param promptHandlers Prompt handlers used by the workflow
   * @return Fully configured Default Workflow
   */
  public DefaultWorkflow createDefaultWorkflow(String name, List<UtteranceProcessor> processors,
                                               List<IntentMatcher> matchers,
                                               List<IntentResolver> resolvers,
                                               List<PromptHandler> promptHandlers) {
    return new DefaultWorkflow(name, processors, matchers, resolvers, promptHandlers);
  }
}
