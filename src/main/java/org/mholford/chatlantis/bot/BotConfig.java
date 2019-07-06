package org.mholford.chatlantis.bot;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.mholford.chatlantis.action.Action;
import org.mholford.chatlantis.action.ActionConfig;
import org.mholford.chatlantis.action.ActionProcessor;
import org.mholford.chatlantis.action.ActionProcessorConfig;
import org.mholford.chatlantis.intent.Intent;
import org.mholford.chatlantis.intent.IntentConfig;
import org.mholford.chatlantis.lookup.FSTLookupTable;
import org.mholford.chatlantis.lookup.FSTLookupTableConfig;
import org.mholford.chatlantis.workflow.Workflow;
import org.mholford.chatlantis.workflow.WorkflowConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Encapsulates user configuration of a Bot.  Will usually be instantiated by
 * deserialization of chatlantis.json config.  Note that init() should be called
 * in order to reflectively create instances of the helper classes.  This is done
 * automatically by Chatlantis during its initialization.
 */
public class BotConfig {
  private String name;
  
  @JsonProperty("actionProcessors")
  private List<ActionProcessorConfig> actionProcessorConfigs = new ArrayList<>();
  
  @JsonProperty("workflows")
  private List<WorkflowConfig> workflowConfigs;
  
  @JsonProperty("actions")
  private List<ActionConfig> actionConfigs;
  
  @JsonProperty("intents")
  private List<IntentConfig> intentConfigs;
  
  @JsonProperty("lookup")
  private FSTLookupTableConfig fstLookupTableConfig;
  
  private final BotFactory bf = BotFactory.get();
  
  /**
   * Instantiate the helper classes of the Bot using reflection.  These include:
   * ActionProcessors, Workflows, Actions, Intents and the FSTLookupTable.  Once
   * these are instantiated, a new Bot can be provisioned by the BotFactory.
   * @return Configured Bot
   * @throws ReflectiveOperationException If any of the helper classes couldn't be instantiated
   * @throws IOException
   */
  public Bot init() throws ReflectiveOperationException, IOException {
    List<ActionProcessor> processors = new ArrayList<>();
    if (actionProcessorConfigs.size() < 1) {
      processors.add(ActionProcessorConfig.getDefault());
    } else {
      for (ActionProcessorConfig apc : actionProcessorConfigs) {
        processors.add(apc.init());
      }
    }
    List<Workflow> workflows = new ArrayList<>();
    for (WorkflowConfig wc : workflowConfigs) {
      workflows.add(wc.init());
    }
    Map<String, Action> actionMap = new HashMap<>();
    for (ActionConfig ac : actionConfigs) {
      actionMap.put(ac.getName(), ac.init());
    }
    Map<String, Intent> intentMap = new HashMap<>();
    for (IntentConfig ic : intentConfigs) {
      intentMap.put(ic.getName(), ic.init(actionMap));
    }
    FSTLookupTable fstLookupTable = new FSTLookupTable();
    fstLookupTable.init(fstLookupTableConfig.getProps());
    return bf.createBot(name, workflows, processors, actionMap, intentMap, fstLookupTable);
  }
  
  /**
   * Gets the name of the Bot
   * @return Name of bot
   */
  public String getName() {
    return name;
  }
  
  /**
   * Sets the name of the Bot to the specified value
   * @param name Name of bot
   */
  public void setName(String name) {
    this.name = name;
  }
  
  /**
   * Gets the Config objects used to instantiate ActionProcessors.
   * @return List of configs
   */
  public List<ActionProcessorConfig> getActionProcessorConfigs() {
    return actionProcessorConfigs;
  }
  
  /**
   * Sets the list of Config objects used to instantiate Actions Processors to the specified
   * value
   * @param actionProcessorConfigs List of config objects
   */
  public void setActionProcessorConfigs(List<ActionProcessorConfig> actionProcessorConfigs) {
    this.actionProcessorConfigs = actionProcessorConfigs;
  }
  
  /**
   * Gets the Config objects used to instantiate Workflows
   * @return List of configs
   */
  public List<WorkflowConfig> getWorkflowConfigs() {
    return workflowConfigs;
  }
  
  /**
   * Set the list of Config objects used to instantiate Workflows to the specified value
   * @param workflowConfigs List of configs
   */
  public void setWorkflowConfigs(List<WorkflowConfig> workflowConfigs) {
    this.workflowConfigs = workflowConfigs;
  }
  
  /**
   * Gets the Config objects used to instantiated Actions
   * @return List of configs
   */
  public List<ActionConfig> getActionConfigs() {
    return actionConfigs;
  }
  
  /**
   * Sets the list of Config objects used to instantiate Actions to the specified value
   * @param actionConfigs List of configs
   */
  public void setActionConfigs(List<ActionConfig> actionConfigs) {
    this.actionConfigs = actionConfigs;
  }
  
  /**
   * Gets the list of config objects used to instantiate Intents
   * @return List of configs
   */
  public List<IntentConfig> getIntentConfigs() {
    return intentConfigs;
  }
  
  /**
   * Sets the list of Config objects used to instantiate Intents to the specified value
   * @param intentConfigs List of configs
   */
  public void setIntentConfigs(List<IntentConfig> intentConfigs) {
    this.intentConfigs = intentConfigs;
  }
  
  /**
   * Gets the config object used to instantiate the lookup table
   * @return Config object
   */
  public FSTLookupTableConfig getFstLookupTableConfig() {
    return fstLookupTableConfig;
  }
  
  /**
   * Sets the config object used to instantiate the lookup table to the specified value
   * @param fstLookupTableConfig Config object
   */
  public void setFstLookupTableConfig(FSTLookupTableConfig fstLookupTableConfig) {
    this.fstLookupTableConfig = fstLookupTableConfig;
  }
}
