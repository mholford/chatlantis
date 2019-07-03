package org.mholford.chatlantis.lookup;

import org.mholford.chatlantis.Utils;

import java.util.EnumSet;
import java.util.List;

/**
 * Represents a wildcard in LUT generation clause.  It has a set of GenAttr
 * attributes and a property (keep) which determines whether what the
 * wildcard replaced is kept (usually to be incorporated in the payload of
 * the Lookup).  Wildcards will be permuted as "*" always.
 */
public class WildcardConf extends MarkerConf implements Utils {
  
  /**
   * Creates a WildcardConf with the specified attributes and retention policy
   * @param keep Whether to keep what the wildcard replaces
   * @param attrs Attribute set
   */
  public WildcardConf(boolean keep, EnumSet<GenAttr> attrs) {
    super(keep, attrs);
  }
  
  /**
   * Creates a WildcardConf with specified retention policy and no attributes.
   * @param keep Whether to keep what is replaced by the wildcard
   */
  public WildcardConf(boolean keep) {
    super(keep, EnumSet.noneOf(GenAttr.class));
  }
  
  @Override
  public List<ClausePermutation> getPermutations() {
    return listOf(new ClausePermutation("*", listOf(this)));
  }
}
