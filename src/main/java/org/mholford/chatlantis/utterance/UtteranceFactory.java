package org.mholford.chatlantis.utterance;

import org.mholford.chatlantis.Conversation;
import org.mholford.chatlantis.Utils;
import org.mholford.chatlantis.context.Context;
import org.mholford.chatlantis.context.ContextFactory;
import org.mholford.fstdict.Range;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Singleton factory to build new Utterances.  These can be built from a literal string
 * or from a list of tokens.  When an Utterance is associated with an existing Conversation,
 * certain parts of Conversation context are copied into Utterance context.  See the javadoc
 * on createNew() or create() for details.
 */
public class UtteranceFactory implements Utils {
  private static UtteranceFactory INSTANCE;
  
  private UtteranceFactory() {
  
  }
  
  /**
   * Gets the singleton instance of the factory, creating it if necessary
   * @return singleton factory instance
   */
  public static UtteranceFactory get() {
    if (INSTANCE == null) {
      INSTANCE = new UtteranceFactory();
    }
    return INSTANCE;
  }
  
  /**
   * Creates a new Utterance from the literal input and associates it with the specified
   * conversation. This will copy current intent and prompt from Conversation context into
   * the context of the new Utterance.  Partial objects in Conversation context are copied
   * into the objects map in Utterance context.
   * @param input Literal input
   * @param conversation Conversation to associate Utterance with
   * @return new Utterance
   */
  public Utterance createNew(String input, Conversation conversation) {
    List<Utterance.TokenRange> tokenRanges = tokenRanges(getTokens(input));
    Context newUttContext = conversation != null ? applyConversationContext(conversation) :
        ContextFactory.get().newContext();
    return new Utterance(tokenRanges, newUttContext, conversation, input);
  }
  
  /**
   * Creates a new Utterance from the specified tokens and associates it with the specified
   * conversation.  This will copy current intent and prompt from Conversation context into
   * the context of the new Utterance.  Partial objects in Conversation context are copied
   * into the objects map in Utterance context.
   * @param tokens Tokens to build Utterance from
   * @param conversation Conversation to associate Utterance with
   * @return new Utterance
   */
  public Utterance create(List<Token> tokens, Conversation conversation) {
    Context newUttContext = conversation != null ? applyConversationContext(conversation) :
        ContextFactory.get().newContext();
    return new Utterance(tokenRanges(tokens), newUttContext, conversation, "");
  }
  
  private Context applyConversationContext(Conversation conv) {
    Context c = ContextFactory.get().newContext();
    Context convCtx = conv.getContext();
    c = c.put("/objects", convCtx.get("/partials"));
    c = c.put("/intent", convCtx.get("/intent"));
    c = c.put("/prompt", convCtx.get("/prompt"));
    return c;
  }
  
  private List<Token> getTokens(String input) {
    List<String> tokens = tokenize(input, TokenizerMode.STANDARD);
    final TokenFactory tf = TokenFactory.get();
    return map(tokens, t -> tf.createLiteral(t));
  }
  
  private List<Utterance.TokenRange> tokenRanges(List<Token> tokens) {
    return IntStream.range(0, tokens.size())
        .mapToObj(i -> new Utterance.TokenRange(tokens.get(i), new Range(i, i)))
        .collect(Collectors.toList());
  }
}
