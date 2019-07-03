package org.mholford.chatlantis.validation;

import org.mholford.chatlantis.context.FullContext;

import java.util.HashSet;
import java.util.Set;

/**
 * Validates a slot value to assure it is not null.  This validator supports any slot
 * value type.
 */
public class NotNull implements Validator {
  @Override
  public Set<Violation> validate(Object value, FullContext ctx, String msg, String path,
                                 String errorMessage) {
    Set<Violation> output = new HashSet<>();
    if (value == null) {
      output.add(new Violation(msg, path, errorMessage));
    }
    return output;
  }
}
