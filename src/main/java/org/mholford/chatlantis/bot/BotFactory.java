package org.mholford.chatlantis.bot;

import org.mholford.chatlantis.Utils;
import org.mholford.chatlantis.action.Action;
import org.mholford.chatlantis.action.ActionProcessor;
import org.mholford.chatlantis.intent.Intent;
import org.mholford.chatlantis.lookup.FSTLookupTable;
import org.mholford.chatlantis.workflow.Workflow;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Singleton class to create instances of Bot.  This is usually called by BotConfig during
 * the process of initializing Chatlantis from the chatlantis.json file.
 */
public class BotFactory implements Utils {
  private static BotFactory INSTANCE;
  
  private BotFactory() {
  }
  
  /**
   * Gets the singleton instance, creating it if necessary.
   * @return Singleton instance
   */
  public static BotFactory get() {
    if (INSTANCE == null) {
      INSTANCE = new BotFactory();
    }
    return INSTANCE;
  }
  
  /**
   * Creates a new Bot from the specified helper elements
   * @param name Name of bot
   * @param workflows Configured workflows
   * @param actionProcessors Configured action processors
   * @param actionMap Map of action name -> configured action
   * @param intents Map of intent name -> configured intent
   * @param lookupTable FSTLookupTable
   * @return Fully configured Bot
   * @throws IOException
   */
  public Bot createBot(String name, List<Workflow> workflows,
                       List<ActionProcessor> actionProcessors, Map<String, Action> actionMap,
                       Map<String, Intent> intents, FSTLookupTable lookupTable) throws IOException {
    Bot bot = new Bot(name, workflows, actionProcessors, actionMap, intents, lookupTable);
    return bot;
  }
}
