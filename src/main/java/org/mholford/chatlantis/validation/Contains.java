package org.mholford.chatlantis.validation;

import org.mholford.chatlantis.context.FullContext;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Validates a String to ensure it contains a particular term.  The term to
 * match is specified by the "match" parameter when initializing this Validator.
 * This validator only supports String slots.
 */
public class Contains implements Validator {
  private String match;
  private final String MATCH_PARAM = "match";
  
  @Override
  public Set<Violation> validateString(String value, FullContext ctx, String prompt, String path,
                                       String errorMessage) {
    Set<Violation> output = new HashSet<>();
    if (value == null || !value.contains(match)) {
      output.add(new Violation(prompt, path, errorMessage));
    }
    
    return output;
  }
  
  @Override
  public void init(Map<String, String> props) {
    match = props.get(MATCH_PARAM);
  }
  
  /**
   * Creates a new Contains validator
   */
  public Contains() {
  
  }
  
  /**
   * Gets the term to match on when validating
   * @return Match term
   */
  public String getMatch() {
    return match;
  }
}
