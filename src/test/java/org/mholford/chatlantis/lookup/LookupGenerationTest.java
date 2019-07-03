package org.mholford.chatlantis.lookup;

import org.junit.Before;
import org.junit.Test;
import org.mholford.chatlantis.Utils;
import org.mholford.chatlantis.lookup.instruction.Instruction;
import org.mholford.chatlantis.lookup.instruction.SetIntent;
import org.mholford.chatlantis.lookup.instruction.SetPrompt;
import org.mholford.chatlantis.lookup.instruction.SetStringSlot;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mholford.chatlantis.lookup.GenAttr.OPTIONAL;

public class LookupGenerationTest implements Utils {
  
  private LookupUtil lu;
  private List<String> ticketVariants;
  private List<String> openVariants;
  private List<String> indefArticleVariants;
  private List<String> namedVariants;
  
  @Before
  public void before() {
    lu = new LookupUtil();
    openVariants = listOf("open", "create", "start", "make", "assign");
    indefArticleVariants = listOf("a", "an");
    ticketVariants = listOf("ticket", "jira");
    namedVariants = listOf("called", "named", "with title");
  }
  
  private List<String> toList(Map<String, String> perms) {
    return map(perms.entrySet(), e -> fmt("%s,%s", e.getKey(), e.getValue()));
  }
  
  @Test
  public void testLookupGeneration() {
    ClauseConf createTicket = new ClauseConf("createTicket")
        .addWildcard(false, OPTIONAL)
        .addPhrase("open", openVariants)
        .addPhrase("indefArticle", indefArticleVariants, OPTIONAL)
        .addPhrase("ticket", ticketVariants);
    
    Map<String, String> perms =
        lu.createLookupEntries(createTicket, listOf(new SetIntent("createTicket")));
    assertEquals(60, perms.size());
  }
  
  @Test
  public void testLookupWithOptionalEntity() {
    ClauseConf createTicket = new ClauseConf("createTicket")
        .addWildcard(false, OPTIONAL)
        .addPhrase("open", openVariants)
        .addPhrase("indef", indefArticleVariants, OPTIONAL)
        .addEntity("PRIO", true, OPTIONAL)
        .addPhrase("ticket", ticketVariants);
    
    List<Instruction> instructions = listOf(new SetIntent("createTicket"),
        new SetStringSlot("/objects/ticket.priority", new EntityMarker("PRIO")));
    
    List<String> perms = toList(lu.createLookupEntries(createTicket, instructions));
    assertEquals(120, perms.size());
    assertTrue(perms.contains("open an PRIO_MKR ticket,$utt:/intent.name -> createTicket; " +
        "$utt:/objects/ticket.priority -> PRIO_MKR_0"));
  }
  
  @Test
  public void testMultiSlot() {
    ClauseConf createTicket = new ClauseConf("createTicket")
        .addWildcard(false, OPTIONAL)
        .addPhrase("oper", openVariants)
        .addEntity("EMP", true, OPTIONAL)
        .addPhrase("indef", indefArticleVariants, OPTIONAL)
        .addEntity("PRIO", true, OPTIONAL)
        .addPhrase("ticket", ticketVariants)
        .addSubClause(
            new ClauseConf("tixName")
                .addPhrase("named", namedVariants)
                .addWildcard(true),
            OPTIONAL);
    
    List<Instruction> instructions = listOf(new SetIntent("createTicket"),
        new SetStringSlot("$utt:/objects/ticket.assignee", new EntityMarker("EMP")),
        new SetStringSlot("$utt:/objects/ticket.priority", new EntityMarker("PRIO")),
        new SetStringSlot("$utt:/objects/ticket.title", new WildcardMarker()));
    
    List<String> perms = toList(lu.createLookupEntries(createTicket, instructions));
    assertEquals(960, perms.size());
  }
  
  @Test
  public void testSubClause() {
    ClauseConf cc = new ClauseConf("createTicket")
        .addPhrase("hello", listOf("hello"))
        .addSubClause(
            new ClauseConf("sub")
                .addPhrase("there", listOf("there"))
            , OPTIONAL);
    
    List<Instruction> instructions = listOf(new SetIntent("hi"));
    List<String> perms = toList(lu.createLookupEntries(cc, instructions));
    assertEquals(2, perms.size());
    assertTrue(perms.contains("hello,$utt:/intent.name -> hi"));
    assertTrue(perms.contains("hello there,$utt:/intent.name -> hi"));
  }
  
  @Test
  public void testSimpleWC() {
    ClauseConf simpleWC = new ClauseConf("setPrompt")
        .addWildcard(true);
    List<String> perms = toList(lu.createLookupEntries(simpleWC,
        listOf(new SetPrompt(new WildcardMarker()))));
    assertEquals(1, perms.size());
    String perm = perms.get(0);
    assertEquals("*,$utt:/prompt.value -> WC_0", perm);
  }
  
  @Test
  public void testMultiWC() {
    ClauseConf multiWC = new ClauseConf("setPrompt")
        .addWildcard(false)
        .addPhrase("hey", listOf("hey"))
        .addWildcard(true);
    List<String> perms = toList(lu.createLookupEntries(multiWC,
        listOf(new SetPrompt(new WildcardMarker()))));
    assertEquals(1, perms.size());
    String perm = perms.get(0);
    assertEquals("* hey *,$utt:/prompt.value -> WC_1", perm);
  }
  
  @Test
  public void testSimpleEnt() {
    ClauseConf simpleEnt = new ClauseConf("setPrompt")
        .addEntity("EMP", true);
    List<String> perms = toList(lu.createLookupEntries(simpleEnt,
        listOf(new SetPrompt(new EntityMarker("EMP")))));
    assertEquals(1, perms.size());
    String perm = perms.get(0);
    assertEquals("EMP_MKR,$utt:/prompt.value -> EMP_MKR_0", perm);
  }
  
  @Test
  public void testMultipleEnt() {
    ClauseConf multiEnt = new ClauseConf("multiEnt")
        .addEntity("EMP", false)
        .addPhrase("hey", listOf("hey"))
        .addEntity("EMP", true);
    List<String> perms = toList(lu.createLookupEntries(multiEnt,
        listOf(new SetPrompt(new EntityMarker("EMP")))));
    assertEquals(1, perms.size());
    assertEquals("EMP_MKR hey EMP_MKR,$utt:/prompt.value -> EMP_MKR_1", perms.get(0));
  }
}
