package org.mholford.chatlantis.action;

import org.mholford.chatlantis.Utils;
import org.mholford.chatlantis.bot.Bot;
import org.mholford.chatlantis.context.FullContext;
import org.mholford.chatlantis.context.SubExpression;
import org.mholford.chatlantis.intent.Intent;
import org.mholford.chatlantis.lookup.instruction.CompleteIntent;
import org.mholford.chatlantis.lookup.instruction.Instruction;
import org.mholford.chatlantis.lookup.instruction.InstructionContext;
import org.mholford.chatlantis.lookup.instruction.SetStringSlot;
import org.mholford.chatlantis.validation.Constraint;
import org.mholford.chatlantis.validation.Validator;
import org.mholford.chatlantis.validation.Violation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChangeTicketAction implements Action, Utils {
  @Override
  public List<Instruction> act(FullContext ctx, Bot bot) {
    boolean valid = true;
    StringBuilder errMsg = new StringBuilder();
    String slotRef = (String) ctx.get("$utt:/objects/changeTicket.property");
    String slotValue = (String) ctx.get("$utt:/objects/changeTicket.value");
    Intent createTicketIntent = bot.getIntent("createTicket");
    List<Constraint> constraints = createTicketIntent.getConstraintsForSlot(slotRef);
    for (Constraint c : constraints) {
      Validator v = c.getValidator();
      Set<Violation> violations = v.validateString(slotValue, ctx, c.getPrompt(), c.getPath(),
          c.getErrorMessage());
      for (Violation violation : violations) {
        valid = false;
        errMsg.append(violation.getErrorMessage());
      }
    }
    if (!valid) {
      return listOf(new SetStringSlot("/action.spoken", errMsg.toString()));
    } else {
      SubExpression subEx = getSubEx(slotRef);
      InstructionContext ic = subEx.getInstructionContext();
      String subRef = subEx.getExpr();
      // Remove all slots dedicated to changeTicket intent
      List<Instruction> output = new ArrayList<>();
      //Intent changeTicketIntent = bot.getIntent("changeTicket");
      //output.addAll(map(changeTicketIntent.getObjectSlots(),
      //    s -> new RemoveSlot(UTTERANCE, "/objects" + s)));
      output.addAll(listOf(new SetStringSlot(ic, subRef, slotValue), new CompleteIntent()));
      return output;
    }
  }
  
  @Override
  public void init(Map<String, String> map) {
  
  }
}
