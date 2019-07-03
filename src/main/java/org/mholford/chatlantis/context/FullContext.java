package org.mholford.chatlantis.context;

import org.mholford.chatlantis.Utils;
import org.mholford.chatlantis.lookup.instruction.Instruction;

import java.util.List;

/**
 * Represents a "full" snapshot of Contexts across the application.  It is composed of
 * User context, Conversation context and Utterance context.  A FullContext snapshot is
 * created at various points of the Utterance lifecycle.  FullContext gives objects like
 * IntentResolvers and ActionProcessors a chance to read Contexts across the application.
 * <p>
 *   Like the Contexts of which it is composed, FullContext is immutable.  Methods are
 *   provided to allow adding/removing from a FullContext.  These will return a new FullContext
 *   with the changes reflected.
 * </p>
 * <p>
 *   Instructions and Context DSL that are sent to FullContext must be sure to specify which
 *   of the underlying context is referred to.  This is encapsulated in the Instruction definition
 *   but for Context DSL, the path must be prepended by a Context label.  These are:
 *   <table><tr><th>Context</th><th>Label</th></tr>
 *   <tr><td>User</td><td>$USER:</td></tr>
 *   <tr><td>Conversation</td><td>$CONV:</td></tr>
 *   <tr><td>Utterance</td><td>$UTT:</td></tr></table>
 * </p>
 */
public class FullContext implements Utils {
  
  private final Context userContext;
  private final Context conversationContext;
  private final Context utteranceContext;
  public static final String UTT = "utt";
  public static final String CONV = "conv";
  public static final String USER = "user";
  
  /**
   * Creates a Full Context "snapshot" including the specified Contexts
   * @param userContext User context
   * @param conversationContext Conversation context
   * @param utteranceContext Utterance context
   */
  public FullContext(Context userContext, Context conversationContext, Context utteranceContext) {
    this.userContext = userContext;
    this.conversationContext = conversationContext;
    this.utteranceContext = utteranceContext;
  }
  
  /**
   * Returns the user context
   * @return User context
   */
  public Context getUserContext() {
    return userContext;
  }
  
  /**
   * Returns the Conversation context
   * @return Conversation context
   */
  public Context getConversationContext() {
    return conversationContext;
  }
  
  /**
   * Returns the Utterance context
   * @return Utterance context
   */
  public Context getUtteranceContext() {
    return utteranceContext;
  }
  
  /**
   * Updates the Full context "snapshot" from the specified list of instructions.  Internally,
   * the Instructions are converted to Context DSL statements.  These are split into puts and
   * removes and passed to the appropriate methods for execution.  If the Instructions have
   * preExec events, they are executed immediately before the Instruction is applied to the
   * Full Context.  If the Instructions have postExec events, they are executed immediately
   * after the Instruction is applied to the Full Context.
   * <p>
   *   <b>NB:</b> If a put Instruction has it's payload enclosed by {}, the value in brackets
   *   will be used as a Context DSL path and the value at that path will be put
   * </p>
   * @param instructions List of Instruction objects
   * @return FullContext with Instructions applied
   */
  public FullContext update(List<Instruction> instructions) {
    if (instructions == null || instructions.size() <= 0) {
      return this;
    }
    
    FullContext fc = this;
    for (Instruction inst : instructions) {
      String cmd = inst.output();
      if (cmd != null) {
        cmd = cmd.trim();
        if (cmd.contains("->")) {
          String[] kv = cmd.split("->");
          Object putVal = kv[1].trim();
          String putValString = (String) putVal;
          if (putValString.startsWith("{") && putValString.endsWith("}")) {
            putValString = putValString.substring(1, putValString.length() - 1).trim();
            putVal = fc.get(putValString);
          }
    
          fc = inst.preExec(fc);
          fc = fc.put(kv[0].trim(), putVal);
          fc = inst.postExec(fc);
        } else if (cmd.contains("-!")) {
          String[] kv = cmd.split("-!");
          fc = inst.preExec(fc);
          fc = fc.remove(kv[1].trim());
          fc = inst.postExec(fc);
        }
      }
    }
    return fc;
  }
  
  /**
   * Puts the specified value in the Context at the Context DSL path specified.  The path
   * must contain the "context label" to know which of the sub-context to put the value in.
   * The method passes the put call to the appropriate sub-context.
   * @param key Context DSL path where to place the value
   * @param value Value to put in Context
   * @return FullContext "snapshot" with put incorporated.
   */
  public FullContext put(String key, Object value) {
    Context uttctx = utteranceContext;
    Context convctx = conversationContext;
    Context userctx = userContext;
    SubExpression subEx = getSubEx(key);
    switch (subEx.getContext().toLowerCase()) {
      case UTT:
        uttctx = utteranceContext.put(subEx.getExpr(), value);
        break;
      case CONV:
        convctx = conversationContext.put(subEx.getExpr(), value);
        break;
      case USER:
        userctx = userContext.put(subEx.getExpr(), value);
        break;
      default:
        throw new ContextTraversalException("Unknown context label: " + subEx.getExpr());
    }
    return new FullContext(userctx, convctx, uttctx);
  }
  
  /**
   * Removes the specified value from the Context at the Context DSL path specified.  The path
   * must contain the "context label".  The method passes the remove call to the appropriate
   * sub-context.
   * @param key Context DSL path where to remove from
   * @return Full Context "snapshot" with delete incorporated
   */
  public FullContext remove(String key) {
    Context uttctx = utteranceContext;
    Context convctx = conversationContext;
    Context userctx = userContext;
    SubExpression subEx = getSubEx(key);
    switch (subEx.getContext().toLowerCase()) {
      case UTT:
        uttctx = utteranceContext.remove(subEx.getExpr());
        break;
      case CONV:
        convctx = conversationContext.remove(subEx.getExpr());
        break;
      case USER:
        userctx = userContext.remove(subEx.getExpr());
        break;
      default:
        throw new ContextTraversalException("Unknown context label: " + subEx.getContext());
    }
    return new FullContext(userctx, convctx, uttctx);
  }
  
  /**
   * Gets the value at the specified Context DSL path.  The path must contain the "context label".
   * This method passes the get call to the appropriate sub-context.
   * @param path Context DSL path to item to get
   * @return Object at path
   */
  public Object get(String path) {
    SubExpression subEx = getSubEx(path);
    switch (subEx.getContext().toLowerCase()) {
      case UTT:
        return getUtteranceContext().get(subEx.getExpr());
      case CONV:
        return getConversationContext().get(subEx.getExpr());
      case USER:
        return getUserContext().get(subEx.getExpr());
      default:
        throw new ContextTraversalException("Unknown context label: " + subEx.getContext());
    }
  }
  
  /**
   * Gets the value at the specified Context DSL path or, if that value is null, returns
   * the alternative specified object instead.
   * @param path Context DSL path to item
   * @param orElse What to return if value is not in Context
   * @return Context value or alternative
   */
  public Object getOrElse(String path, Object orElse) {
    Object o = get(path);
    return o != null ? o : orElse;
  }
}
