package org.mholford.chatlantis.validation;

import org.mholford.chatlantis.context.FullContext;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Validates a particular Constraint against the current FullContext snapshot.  The main
 * validate() method checks the value type and calls a more specific method based on that
 * type (e.g. validateList, validateMap, etc.)  By default these more specific methods
 * throw an UnsupportedOperationException.  This allows individual Validators to only
 * support particular value types.  Validators are initialized from a map of properties.
 * This typically occurs when the user constraints configuration are initialized during
 * Chatlantis' initialization from config files.
 */
public interface Validator {

  /**
   * Validates a Constraint and answers what Violations were found.  Based on type of value
   * supplied, this method calls a more specific validateXXX function.
   * @param value Value being checked
   * @param ctx Context snapshot
   * @param msg Prompt message
   * @param path Context DSL path to the slot which is constrained
   * @param errorMessage Detailed error message
   * @return Set of violations encountered
   */
  default Set<Violation> validate(Object value, FullContext ctx, String msg, String path,
                                  String errorMessage) {
    if (value instanceof Map) {
      return validateMap((Map) value, ctx, msg, path, errorMessage);
    } else if (value instanceof List) {
      return validateList((List) value, ctx, msg, path, errorMessage);
    } else if (value instanceof String) {
      return validateString((String) value, ctx, msg, path, errorMessage);
    } else if (value instanceof Number){
      return validateNumber((Number) value, ctx, msg, path, errorMessage);
    } else if (value instanceof Boolean) {
      return validateBoolean((Boolean) value, ctx, msg, path, errorMessage);
    } else {
      String vv = value == null ? null : value.toString();
      return validateString(vv, ctx, msg, path, errorMessage);
    }
  }
  
  /**
   * Validates a Constraint on a List.  Unless overridden, this method throws an
   * UnsupportedOperationException.
   * @param value List we are validating
   * @param ctx Context snapshot
   * @param msg Prompt message
   * @param path Context DSL path to slot being constrained
   * @param errorMessage Detailed message
   * @return All violations
   */
  default Set<Violation> validateList(List value, FullContext ctx, String msg, String path,
                                      String errorMessage) {
    throw new UnsupportedOperationException("Validation of lists is not supported");
  }
  
  /**
   * Validates a Constraint on a Map.  Unless overridden, this method throws an
   * UnsupportedOperationException.
   * @param value Map we are validating
   * @param ctx Context snapshot
   * @param msg Prompt message
   * @param path Context DSL path to slot being constrained
   * @param errorMessage Detailed message
   * @return all violations
   */
  default Set<Violation> validateMap(Map value, FullContext ctx, String msg, String path,
                                     String errorMessage) {
    throw new UnsupportedOperationException("Validation of maps is not supported");
  }
  
  /**
   * Validates a Constraint on a String.  Unless overridden, this method throws an
   * UnsupportedOperationException.
   * @param value String we are validating
   * @param ctx Context snapshot
   * @param msg Prompt message
   * @param path Context DSL path to slot being constrained
   * @param errorMessage Detailed message
   * @return all violations
   */
  default Set<Violation> validateString(String value, FullContext ctx, String msg, String path,
                                        String errorMessage) {
    throw new UnsupportedOperationException("Validation of strings is not supported");
  }
  
  /**
   * Validates a Constraint on a Number.  Unless overridden, this method throws an
   * UnsupportedOperationException.
   * @param value Number we are validating
   * @param ctx Context snapshot
   * @param msg Prompt message
   * @param path Context DSL path to slot being constrained
   * @param errorMessage Detailed message
   * @return all violations
   */
  default Set<Violation> validateNumber(Number value, FullContext ctx, String msg, String path,
                                        String errorMessage) {
    throw new UnsupportedOperationException("Validation of numbers is not supported");
  }
  
  /**
   * Validates a Constraint on a Boolean.  Unless overridden, this method throws an
   * UnsupportedOperationException.
   * @param value Boolean we are validating
   * @param ctx Context snapshot
   * @param msg Prompt message
   * @param path Context DSL path to slot being constrained
   * @param errorMessage Detailed message
   * @return all violations
   */
  default Set<Violation> validateBoolean(Boolean value, FullContext ctx, String msg, String path,
                                         String errorMessage) {
    throw new UnsupportedOperationException("Validation of booleans is not supported");
  }
  
  /**
   * Initializes the Validator from a map of properties.  Unless overriden, this is a no-op.
   * @param props Map of properties
   * @throws IOException If something went wrong
   */
  default void init(Map<String, String> props) throws IOException {}
}
