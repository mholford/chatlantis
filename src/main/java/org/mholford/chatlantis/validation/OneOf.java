package org.mholford.chatlantis.validation;

import org.mholford.chatlantis.Utils;
import org.mholford.chatlantis.context.FullContext;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Validates a String value to assure it is one of a list of terms.  The list of legal
 * terms is provided from a dictionary file specified by the "dict" parameter.  The terms
 * in this file are read into the Validator's memory when the Validator is initialized
 * during Chatlantis startup.  This Validator only support String slot types.
 */
public class OneOf implements Validator, Utils {
  private List<String> legalValues;
  private final String DICT_PARAM = "dict";
  
  /**
   * Creates a new OneOf validator
   */
  public OneOf() {
  }
  
  @Override
  public Set<Violation> validateString(String value, FullContext ctx, String msg, String path,
                                       String errorMessage) {
    Set<Violation> output = new HashSet<>();
    
    if (!legalValues.contains(value)) {
      output.add(new Violation(msg, path, errorMessage));
    }
    
    return output;
  }
  
  @Override
  public void init(Map<String, String> props) throws IOException {
    legalValues = new ArrayList<>();
    String valFile = props.get(DICT_PARAM);
    if (getResource(valFile) != null) {
      legalValues.addAll(readLinesIntoList(new InputStreamReader(getResource(valFile))));
    }
  }
  
  /**
   * Gets the list of terms the value can be
   * @return list of terms
   */
  public List<String> getLegalValues() {
    return legalValues;
  }
}
