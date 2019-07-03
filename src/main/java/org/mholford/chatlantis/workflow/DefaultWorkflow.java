package org.mholford.chatlantis.workflow;

import org.mholford.chatlantis.Utils;
import org.mholford.chatlantis.intent.IntentMatcher;
import org.mholford.chatlantis.intent.IntentResolver;
import org.mholford.chatlantis.prompt.PromptHandler;

import java.util.List;

/**
 * A basic implementation of the Workflow class which stores associated helper classes
 * (utterance processors, intent matchers, intent resolvers, prompt handlers).  It is
 * expected that clients needing specialized Workflows will extend this class.  This
 * class is functionally immutable.  Instances of it should be created using the
 * DefaultWorkflowFactory.  This occurs automatically behind the scenes when Chatlantis
 * is initialized from the chatlantis.json file.
 */
public class DefaultWorkflow implements Workflow, Utils {
  private final String name;
  private final List<UtteranceProcessor> processors;
  private final List<IntentMatcher> matchers;
  private final List<IntentResolver> resolvers;
  private final List<PromptHandler> promptHandlers;
  
  DefaultWorkflow(String name, List<UtteranceProcessor> processors, List<IntentMatcher> matchers,
                  List<IntentResolver> resolvers, List<PromptHandler> promptHandlers) {
    this.name = name;
    this.processors = processors;
    this.matchers = matchers;
    this.resolvers = resolvers;
    this.promptHandlers = promptHandlers;
  }
  
  @Override
  public List<IntentMatcher> getMatchers() {
    return matchers;
  }
  
  @Override
  public List<IntentResolver> getResolvers() {
    return resolvers;
  }
  
  @Override
  public List<PromptHandler> getPromptHandlers() {
    return promptHandlers;
  }
  
  @Override
  public List<UtteranceProcessor> getProcessors() {
    return processors;
  }
  
  @Override
  public String getName() {
    return name;
  }
}
