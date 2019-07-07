package org.mholford.chatlantis.bot;

import com.google.common.base.Strings;
import org.mholford.chatlantis.Conversation;
import org.mholford.chatlantis.User;
import org.mholford.chatlantis.Utils;
import org.mholford.chatlantis.action.Action;
import org.mholford.chatlantis.action.ActionProcessor;
import org.mholford.chatlantis.action.ActionResponse;
import org.mholford.chatlantis.context.FullContext;
import org.mholford.chatlantis.intent.Intent;
import org.mholford.chatlantis.lookup.FSTLookupTable;
import org.mholford.chatlantis.lookup.instruction.Instruction;
import org.mholford.chatlantis.lookup.instruction.InstructionContext;
import org.mholford.chatlantis.lookup.instruction.SetStringSlot;
import org.mholford.chatlantis.utterance.Utterance;
import org.mholford.chatlantis.workflow.Response;
import org.mholford.chatlantis.workflow.Workflow;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * The main workhorse of Chatlantis, the Bot class governs the processing of an Utterance
 * and generation of a response.  The Bot class also maintains whatever helper classes are
 * associated with a particular bot.  These include:<br/><ul>
 *   <li>processing Workflows</li>
 *   <li>actionProcessors</li>
 *   <li>Actions</li>
 *   <li>Intents</li>
 *   <li>the FSTLookupTable</li>
 * </ul><br/>
 * Bot's are typically configured via the chatlantis.json file, with the BotConfig class
 * creating the Bot instance.  As Bot's constructor is private, the BotFactory should be
 * used to create new Bots.  The Bot class is functionally immutable, as all its fields
 * are final.
 */
public class Bot implements Utils {
  private final List<Workflow> workflows;
  private final List<ActionProcessor> actionProcessors;
  private final Map<String, Action> actions;
  private final Map<String, Intent> intents;
  private final FSTLookupTable lookupTable;
  private final String name;
  
  Bot(String name, List<Workflow> workflows, List<ActionProcessor> actionProcessors,
      Map<String, Action> actions, Map<String, Intent> intents, FSTLookupTable lookupTable) {
    this.name = name;
    this.workflows = workflows;
    this.actionProcessors = actionProcessors;
    this.actions = actions;
    this.intents = intents;
    this.lookupTable = lookupTable;
  }
  
  /**
   * Process user Utterance and return a String to be "spoken" by Chatlantis as a response.
   * The process can be summarized as follows:<br/><ul>
   *   <li>Pick an Utterance processing workflow</li>
   *   <li>Use the workflow to get a Response object</li>
   *   <li>Update context with the Instructions in the Response</li>
   *   <li>If the response contains Actions to be performed, pick an ActionProcessor</li>
   *   <li>Perform each Action, updating the Context and spoken output</li>
   *   <li>Update the Conversation and User Contexts from the final Context snapshot</li>
   *   <li>Utterance context is not persisted between invocations</li>
   * </ul>
   * @param u
   * @return
   * @throws IOException
   */
  public String answer(Utterance u) throws IOException {
    Conversation conv = u.getConversation();
    User user = conv.getUser();
  
    Workflow wf = pickWorkflow(u);
  
    Response resp = wf.process(u, conv, this);
    StringBuilder reply = new StringBuilder(resp.getSpokenResponse());
    ActionProcessor ap = pickActionProcessor(resp);
    FullContext ctxSnapshot = resp.getContext();
    ctxSnapshot = ctxSnapshot.update(resp.getInstructions());
    for (Action a : resp.getActions()) {
      ActionResponse ar = ap.process(a, ctxSnapshot, this, u);
      String spokenResponse = ar.getSpokenResponse();
      if (!Strings.isNullOrEmpty(spokenResponse)) {
        if (reply.length() > 0) {
          reply.append("\n");
        }
        reply.append(ar.getSpokenResponse());
      }
      ctxSnapshot = ar.getContext();
    }
    List<Instruction> instructions = listOf(
        new SetStringSlot(InstructionContext.CONVERSATION, "/partials", "$utt:/objects", true)
    );
    if (ctxSnapshot.get("$utt:/intent.name") != null) {
      instructions.add(new SetStringSlot(InstructionContext.CONVERSATION, "/intent.name",
          (String) ctxSnapshot.get("$utt:/intent.name")));
    }
    ctxSnapshot = ctxSnapshot.update(instructions);
    conv.setContext(ctxSnapshot.getConversationContext());
    user.setContext(ctxSnapshot.getUserContext());
    return reply.toString();
  }
  
  /**
   * Pick the best workflow for processing the specified Utterance.  Currently, just
   * returns the first configured workflow.  We don't really support multiple workflows
   * per bot in any concrete way.
   * @param u Utterance
   * @return Best workflow
   */
  public Workflow pickWorkflow(Utterance u) {
    return workflows.get(0);
  }
  
  /**
   * Pick the best ActionProcessor for processing the specified Response.  Currently,
   * ust returns the first configured ActionProcessor.  Multiple ActionProcessors per
   * workflow are not really supported yet.
   * @param resp Response to process Actions from
   * @return Best ActionProcessor
   */
  public ActionProcessor pickActionProcessor(Response resp) {
    return actionProcessors.get(0);
  }
  
  /**
   * Gets the name of the Bot
   * @return Name of bot
   */
  public String getName() {
    return name;
  }
  
  /**
   * Returns all Workflows configured for this Bot
   * @return List of Workflows
   */
  public List<Workflow> getWorkflows() {
    return workflows;
  }
  
  /**
   * Returns all ActionProcessors configured for this Bot
   * @return List of ActionProcessors
   */
  public List<ActionProcessor> getActionProcessors() {
    return actionProcessors;
  }
  
  /**
   * Returns the FSTLookupTable configured for this Bot
   * @return Lookup table
   */
  public FSTLookupTable getLookupTable() {
    return lookupTable;
  }
  
  /**
   * Returns the Actions configured for this Bot, mapped by their name
   * @return Map of action name -> Action
   */
  public Map<String, Action> getActions() {
    return actions;
  }
  
  /**
   * Returns the Intents configured for this Bot, mapped by their name
   * @return Map of intent name -> Action
   */
  public Map<String, Intent> getIntents() {
    return intents;
  }
  
  /**
   * Returns the configured Intent with the specified name
   * @param name Name of intent
   * @return The Intent with the specified name
   */
  public Intent getIntent(String name){
    return intents.get(name);
  }
}
