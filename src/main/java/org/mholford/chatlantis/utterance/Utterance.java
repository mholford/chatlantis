package org.mholford.chatlantis.utterance;

import org.mholford.chatlantis.Conversation;
import org.mholford.chatlantis.Utils;
import org.mholford.chatlantis.context.Context;
import org.mholford.fstdict.Range;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Utterance represents a complete statement by a user, in any of various
 * states of processing.  An Utterance consists of a List of TokenRanges.
 * TokenRange is a simple POJO combining a Token with its numeric range within
 * the Utterance.  An Utterance is associated to a Conversation, but also
 * maintains its own Context.  Because each User statement in a Conversation
 * results in a new Utterance, the previous Utterance goes away after Chatlantis
 * has responded to it.  Thus, anything you want to save from an Utterance should
 * be moved into Conversation context.  This is typically the job of the IntentResolver.
 * Utterances are immutable.  New instances should be creating by using the singleton
 * UtteranceFactory which provides a handful of utility methods for this purpose.
 */
public class Utterance implements Utils {
  
  /**
   * A simple POJO to aggregate a token with its Range within the Utterance
   */
  public static class TokenRange {
    final Token token;
    final Range range;
    
    TokenRange(Token token, Range range) {
      this.token = token;
      this.range = range;
    }
  
    /**
     * Gets the token
     * @return Token
     */
    public Token getToken() {
      return token;
    }
  
    /**
     * Gets the Range object
     * @return Range
     */
    public Range getRange() {
      return range;
    }
  }
  private Context context;
  private final Conversation conversation;
  private final List<TokenRange> tokenRanges;
  private final String originalInput;
  
  Utterance(List<TokenRange> tokenRanges, Context context, Conversation conversation,
            String originalInput) {
    this.tokenRanges = tokenRanges;
    this.context = context;
    this.conversation = conversation;
    this.originalInput = originalInput;
  }
  
  /**
   * Gets a list of all the "literal token spans", i.e. sets of contiguous literal tokens
   * @return List of spans
   */
  public List<LiteralTokenSpan> findLiteralTokenSpans() {
    List<LiteralTokenSpan> spans = new ArrayList<>();
    List<LiteralToken> currLTS = new ArrayList<>();
    List<Token> tokens = toTokens(tokenRanges);
    
    for (int i = 0; i < tokens.size(); i++) {
      Token tok = tokens.get(i);
      if (!(tok instanceof LiteralToken)) {
        if (currLTS.size() > 0) {
          spans.add(new LiteralTokenSpan(i - currLTS.size(), currLTS));
          currLTS = new ArrayList<>();
        }
      } else {
        currLTS.add((LiteralToken) tok);
      }
    }
    if (currLTS.size() > 0) {
      spans.add(new LiteralTokenSpan(tokens.size() - currLTS.size(), currLTS));
    }
    
    return spans;
  }
  
  private List<Token> toTokens(List<TokenRange> tokenRanges) {
    return map(tokenRanges, TokenRange::getToken);
  }
  
  /**
   * Gets the Context associated with this Utterance
   * @return context object
   */
  public Context getContext() {
    return context;
  }
  
  /**
   * Gets the Conversation associated with this Utterance
   * @return Conversation object
   */
  public Conversation getConversation() {
    return conversation;
  }
  
  /**
   * Get all the token/ranges that make up this Utterance
   * @return List of token/ranges
   */
  public List<TokenRange> getTokenRanges() {
    return tokenRanges;
  }
  
  /**
   * Get the user's original input, prior to any Utterance processing
   * @return Original string
   */
  public String getOriginalInput() {
    return originalInput;
  }
  
  /**
   * Get the number of token/ranges that compose this Utterance
   * @return
   */
  public int size() {
    return tokenRanges.size();
  }
  
  /**
   * Gets the nth token in the Utterance.
   * @param pos Position of the token in the token/range list
   * @return Token
   */
  public Token getToken(int pos) {
    return tokenRanges.get(pos).token;
  }
  
  @Override
  public String toString() {
    List<Token> tokens = toTokens(tokenRanges);
    StringBuilder sb = new StringBuilder();
    Iterator<Token> tokIter = tokens.iterator();
    while (tokIter.hasNext()) {
      Token tok = tokIter.next();
      sb.append(tok.toString());
      if (tokIter.hasNext()) {
        sb.append(", ");
      }
    }
    return sb.toString();
  }
}
