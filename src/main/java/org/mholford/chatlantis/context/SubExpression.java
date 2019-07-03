package org.mholford.chatlantis.context;

import org.mholford.chatlantis.lookup.instruction.InstructionContext;

/**
 * Representation of a ContextDSL expression which composes it into a context label
 * and a sub-expression.  The class knows about the three types of Context (User,
 * Conversation and Utterance) and has constants for their context label abbreviations.
 */
public class SubExpression {
  private final String context;
  private final String expr;
  public static final String UTT = "utt";
  public static final String CONV = "conv";
  public static final String USER = "user";
  
  /**
   * Constructs a new SubExpression from the given context label and Context DSL expression
   * @param context Label of Context within FullContext
   * @param expr The Context DSL expression to be applied to the Context
   */
  public SubExpression(String context, String expr) {
    this.context = context;
    this.expr = expr;
  }
  
  /**
   * Gets the context label of the Context where the expression should be applied
   * @return Context label
   */
  public String getContext() {
    return context;
  }
  
  /**
   * Gets the Context DSL expression which should be applied
   * @return Context DSL
   */
  public String getExpr() {
    return expr;
  }
  
  /**
   * Returns the actual InstructionContext for the specified context label
   * @return InstructionContext enum
   */
  public InstructionContext getInstructionContext() {
    switch (context) {
      case UTT:
        return InstructionContext.UTTERANCE;
      case CONV:
        return InstructionContext.CONVERSATION;
      case USER:
        return InstructionContext.USER;
      default:
        return null;
    }
  }
}
