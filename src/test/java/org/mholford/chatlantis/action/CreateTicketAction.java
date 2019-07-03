package org.mholford.chatlantis.action;

import org.mholford.chatlantis.Utils;
import org.mholford.chatlantis.bot.Bot;
import org.mholford.chatlantis.context.FullContext;
import org.mholford.chatlantis.lookup.instruction.Instruction;
import org.mholford.chatlantis.lookup.instruction.SetStringSlot;

import java.util.List;
import java.util.Map;


public class CreateTicketAction implements Action, Utils {
  private int numTix;
  
  @Override
  public List<Instruction> act(FullContext ctx, Bot bot) {
    String tixId = fmt("ABC-%03d", ++numTix);
    String response = fmt("Created ticket %s in JIRA", tixId);
    return listOf(new SetStringSlot("/objects/ticket.number", tixId),
        new SetStringSlot("/action.spoken", response));
  }
  
  @Override
  public void init(Map<String, String> map) {
    numTix = 0;
  }
}

