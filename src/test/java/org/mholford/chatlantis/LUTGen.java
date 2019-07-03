package org.mholford.chatlantis;

import org.mholford.chatlantis.lookup.*;
import org.mholford.chatlantis.lookup.instruction.Instruction;
import org.mholford.chatlantis.lookup.instruction.SetIntent;
import org.mholford.chatlantis.lookup.instruction.SetPrompt;
import org.mholford.chatlantis.lookup.instruction.SetStringSlot;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.mholford.chatlantis.lookup.GenAttr.OPTIONAL;

public class LUTGen implements LUTGenerator, Utils {
  private String outputFile = "/home/matt/kmw/chatlantis/src/test/resources/tix-gen-LUT.csv";
  private LookupUtil lu;
  private List<String> ticketVariants;
  private List<String> openVariants;
  private List<String> indefArticleVariants;
  private List<String> namedVariants;
  private List<String> assignVariants;
  
  @Override
  public void generate() throws IOException {
    lu = new LookupUtil();
    openVariants = listOf("open", "create", "start", "make", "assign");
    indefArticleVariants = listOf("a", "an");
    ticketVariants = listOf("ticket", "jira");
    namedVariants = listOf("called", "named", "with title");
    assignVariants = listOf("assign", "give", "assigned", "given");
    
    Map<String, String> allPerms = new TreeMap<>();
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
        new SetStringSlot("/objects/ticket.assignee", new EntityMarker("EMP")),
        new SetStringSlot("/objects/ticket.priority", new EntityMarker("PRIO")),
        new SetStringSlot("/objects/ticket.title", new WildcardMarker()));
    
    allPerms.putAll(lu.createLookupEntries(createTicket, instructions));
    
    ClauseConf changeAssignee1 = new ClauseConf("changeAssignee1")
        .addWildcard(false, OPTIONAL)
        .addPhrase("assign", assignVariants)
        .addWildcard(false, OPTIONAL)
        .addEntity("EMP", true)
        .addWildcard(false, OPTIONAL);
    
    instructions = listOf(
        new SetStringSlot("/objects/changeTicket.property", "$utt:/objects/ticket.assignee"),
        new SetStringSlot("/objects/changeTicket.propertyDisplayName", "assignee"),
        new SetStringSlot("/objects/changeTicket.value", new EntityMarker("EMP")));
    allPerms.putAll(lu.createLookupEntries(changeAssignee1, instructions));
    
    ClauseConf wcPrompt = new ClauseConf("wcPrompt").addWildcard(true);
    allPerms.putAll(lu.createLookupEntries(wcPrompt, listOf(
        new SetPrompt(new WildcardMarker()))));
    
    ClauseConf empPrompt = new ClauseConf("empPrompt").addEntity("EMP", true);
    allPerms.putAll(lu.createLookupEntries(empPrompt, listOf(
        new SetPrompt(new EntityMarker("EMP")))));
    
    ClauseConf prioPrompt = new ClauseConf("prioPrompt").addEntity("PRIO", true);
    allPerms.putAll(lu.createLookupEntries(prioPrompt, listOf(
        new SetPrompt(new EntityMarker("PRIO")))));
    
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {
      for (Map.Entry<String, String> e : allPerms.entrySet()) {
        bw.write(fmt("%s,%s", e.getKey(), e.getValue()));
        bw.write("\n");
        bw.flush();
      }
    }
  }
  
  public static void main(String[] args) {
    try {
      new LUTGen().generate();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
