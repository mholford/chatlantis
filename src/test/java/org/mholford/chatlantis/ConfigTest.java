package org.mholford.chatlantis;

import org.junit.Test;
import org.mholford.chatlantis.action.Action;
import org.mholford.chatlantis.action.ActionProcessor;
import org.mholford.chatlantis.action.CreateTicketAction;
import org.mholford.chatlantis.action.RevalidatingActionProcessor;
import org.mholford.chatlantis.bot.Bot;
import org.mholford.chatlantis.bot.BotRegistry;
import org.mholford.chatlantis.intent.*;
import org.mholford.chatlantis.lookup.FSTLookupTable;
import org.mholford.chatlantis.prompt.DefaultPromptHandler;
import org.mholford.chatlantis.prompt.PromptHandler;
import org.mholford.chatlantis.utterance.UtteranceFactory;
import org.mholford.chatlantis.validation.*;
import org.mholford.chatlantis.workflow.EEUtteranceProcessor;
import org.mholford.chatlantis.workflow.UtteranceProcessor;
import org.mholford.chatlantis.workflow.Workflow;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ConfigTest {
  private final BotRegistry botRegistry = BotRegistry.get();
  private final UtteranceFactory utteranceFactory = UtteranceFactory.get();
  
  @Test
  public void testConfig() throws IOException, ReflectiveOperationException {
    Chatlantis.clear();
    System.setProperty("chatlantis.config", "src/test/resources/chatlantis-simple.json");
    Chatlantis server = Chatlantis.get();
    
    assertEquals(1, botRegistry.size());
    Bot b = botRegistry.find("default");
    
    List<ActionProcessor> actionProcessors = b.getActionProcessors();
    assertEquals(1, actionProcessors.size());
    ActionProcessor ap = actionProcessors.get(0);
    assertTrue(ap instanceof RevalidatingActionProcessor);
    
    List<Workflow> workflows = b.getWorkflows();
    assertEquals(1, workflows.size());
    Workflow w = workflows.get(0);
    assertEquals("default", w.getName());
    
    List<UtteranceProcessor> processors = w.getProcessors();
    assertEquals(1, processors.size());
    UtteranceProcessor up = processors.get(0);
    assertTrue(up instanceof EEUtteranceProcessor);
    EEUtteranceProcessor ee = (EEUtteranceProcessor) up;
    assertEquals("abc-employees.csv", ee.getDictFile());
    assertEquals("EMP", ee.getAlias());
    
    List<IntentMatcher> intentMatchers = w.getMatchers();
    assertEquals(1, intentMatchers.size());
    IntentMatcher im = intentMatchers.get(0);
    assertTrue(im instanceof ExplicitIntentMatcher);
    
    List<IntentResolver> intentResolvers = w.getResolvers();
    assertEquals(1, intentResolvers.size());
    IntentResolver ir = intentResolvers.get(0);
    assertTrue(ir instanceof ValidatingIntentResolver);
    
    List<PromptHandler> promptHandlers = w.getPromptHandlers();
    assertEquals(1, promptHandlers.size());
    PromptHandler ph = promptHandlers.get(0);
    assertTrue(ph instanceof DefaultPromptHandler);
    
    assertEquals(1, b.getActions().size());
    Action a = b.getActions().get("createTicket");
    assertTrue(a instanceof CreateTicketAction);
    
    assertEquals(1, b.getIntents().size());
    Intent i = b.getIntents().get("createTicket");
    assertEquals("createTicket", i.getName());
    assertEquals("You are creating a ticket called <$utt:/objects/ticket.title>, " +
        "assigned to <$utt:/objects/ticket.assignee>, " +
        "with priority of <$utt:/objects/ticket.priority>, " +
        "and an estimate of <$utt:/objects/ticket.estimate>.", i.getSuccessTemplateString());
    List<Action> successActions = i.getSuccessActions();
    assertEquals(1, successActions.size());
    Action sa = successActions.get(0);
    assertTrue(sa instanceof CreateTicketAction);
    assertTrue(i.isRequiresConfirmation());
    
    List<Constraint> constraints = i.getConstraints();
    assertEquals(4, constraints.size());
    Constraint c = constraints.get(0);
    assertEquals("$utt:/objects/ticket.title", c.getPath());
    assertEquals("What should we call this ticket?", c.getPrompt());
    assertTrue(c.getValidator() instanceof NotNull);
    c = constraints.get(1);
    assertEquals("$utt:/objects/ticket.assignee", c.getPath());
    assertEquals("Who should we assign this ticket to?", c.getPrompt());
    Validator v = c.getValidator();
    assertTrue(v instanceof OneOf);
    OneOf oo = (OneOf) v;
    assertEquals(8, oo.getLegalValues().size());
    c = constraints.get(2);
    assertEquals("$utt:/objects/ticket.priority", c.getPath());
    assertEquals("What priority should we set?", c.getPrompt());
    v = c.getValidator();
    assertTrue(v instanceof OneOf);
    oo = (OneOf) v;
    assertEquals(4, oo.getLegalValues().size());
    c = constraints.get(3);
    assertEquals("$utt:/objects/ticket.estimate", c.getPath());
    assertEquals("How long should we estimate?", c.getPrompt());
    v = c.getValidator();
    assertTrue(v instanceof Contains);
    Contains con = (Contains) v;
    assertEquals("hours", con.getMatch());
    
    FSTLookupTable fst = b.getLookupTable();
    String answer = fst.lookup(utteranceFactory.createNew("tix", null));
    assertEquals("$utt:/intent.name -> createTicket", answer);
  }
}
