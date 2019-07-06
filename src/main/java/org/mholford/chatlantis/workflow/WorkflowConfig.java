package org.mholford.chatlantis.workflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.mholford.chatlantis.Utils;
import org.mholford.chatlantis.intent.IntentMatcher;
import org.mholford.chatlantis.intent.IntentMatcherConfig;
import org.mholford.chatlantis.intent.IntentResolver;
import org.mholford.chatlantis.intent.IntentResolverConfig;
import org.mholford.chatlantis.prompt.PromptHandler;
import org.mholford.chatlantis.prompt.PromptHandlerConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates user configuration of a Chatlantis workflow.  It is composed of a name
 * for the workflow and lists of sub-configuration for the four helper classes affiliated
 * with a Workflow: UtteranceProcessors, IntentMatchers, IntentResolvers and PromptHandlers.
 * Upon initialization, this will instantiate each of the helper classes.  WorkflowConfig
 * is usually instantiated through deserialization of the chatlantis.json file.  This occurs
 * when Chatlantis is started up from configuration files.
 */
public class WorkflowConfig implements Utils {
  private String name;
  
  @JsonProperty("utteranceProcessors")
  private List<UtteranceProcessorConfig> processorConfigs = new ArrayList<>();
  
  @JsonProperty("intentMatchers")
  private List<IntentMatcherConfig> matcherConfigs = new ArrayList<>();
  
  @JsonProperty("intentResolvers")
  private List<IntentResolverConfig> resolverConfigs = new ArrayList<>();
  
  @JsonProperty("promptHandlers")
  private List<PromptHandlerConfig> promptHandlerConfigs = new ArrayList<>();
  
  /**
   * Initializes the Workflow by instantiating each of the configured helper classes
   * @return Fully initialized Workflow instance
   * @throws IOException                  If could not read configuration
   * @throws ReflectiveOperationException If could not instantiate helper classes
   */
  public Workflow init() throws IOException, ReflectiveOperationException {
    List<UtteranceProcessor> processors = new ArrayList<>();
    for (UtteranceProcessorConfig upc : processorConfigs) {
      processors.add(upc.init());
    }
    
    List<IntentMatcher> matchers = new ArrayList<>();
    if (matcherConfigs.size() < 1) {
      matchers.add(IntentMatcherConfig.getDefault());
    } else {
      for (IntentMatcherConfig imc : matcherConfigs) {
        matchers.add(imc.init());
      }
    }
    
    List<IntentResolver> resolvers = new ArrayList<>();
    if (resolverConfigs.size() < 1) {
      resolvers.add(IntentResolverConfig.getDefault());
    } else {
      for (IntentResolverConfig irc : resolverConfigs) {
        resolvers.add(irc.init());
      }
    }
    
    List<PromptHandler> promptHandlers = new ArrayList<>();
    if (promptHandlerConfigs.size() < 1) {
      promptHandlers.add(PromptHandlerConfig.getDefault());
    } else {
      for (PromptHandlerConfig phc : promptHandlerConfigs) {
        promptHandlers.add(phc.init());
      }
    }
    return DefaultWorkflowFactory.get().createDefaultWorkflow(
        name, processors, matchers, resolvers, promptHandlers);
  }
  
  /**
   * Gets the name of the workflow
   * @return Name of workflow
   */
  public String getName() {
    return name;
  }
  
  /**
   * Sets the name of the workflow to the specified value
   * @param name Name of workflow
   */
  public void setName(String name) {
    this.name = name;
  }
  
  /**
   * Gets the UtteranceProcessors configured for this workflow
   * @return Utterance Processor configs
   */
  public List<UtteranceProcessorConfig> getProcessorConfigs() {
    return processorConfigs;
  }
  
  /**
   * Sets the UtteranceProcessor configuration for this workflow to the specified
   * @param processorConfigs Utterance Processor configs
   */
  public void setProcessorConfigs(List<UtteranceProcessorConfig> processorConfigs) {
    this.processorConfigs = processorConfigs;
  }
  
  /**
   * Gets the IntentMatchers configured for this workflow
   * @return Intent Matcher configs
   */
  public List<IntentMatcherConfig> getMatcherConfigs() {
    return matcherConfigs;
  }
  
  /**
   * Sets the IntentMatchers configured for this workflow to the specified
   * @param matcherConfigs Intent Matcher configs
   */
  public void setMatcherConfigs(List<IntentMatcherConfig> matcherConfigs) {
    this.matcherConfigs = matcherConfigs;
  }
  
  /**
   * Gets the IntentResolvers configured for this workflow
   * @return Intent Resolver configs
   */
  public List<IntentResolverConfig> getResolverConfigs() {
    return resolverConfigs;
  }
  
  /**
   * Sets the IntentResolvers configured for this workflow to the specified
   * @param resolverConfigs Intent Resolver configs
   */
  public void setResolverConfigs(List<IntentResolverConfig> resolverConfigs) {
    this.resolverConfigs = resolverConfigs;
  }
  
  /**
   * Gets the PromptHandlers configured for this workflow
   * @return Prompt Handler configs
   */
  public List<PromptHandlerConfig> getPromptHandlerConfigs() {
    return promptHandlerConfigs;
  }
  
  /**
   * Sets the PromptHandlers configured for this workflow to the specified
   * @param promptHandlerConfigs Prompt Handler configs
   */
  public void setPromptHandlerConfigs(List<PromptHandlerConfig> promptHandlerConfigs) {
    this.promptHandlerConfigs = promptHandlerConfigs;
  }
}
