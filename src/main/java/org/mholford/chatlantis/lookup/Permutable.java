package org.mholford.chatlantis.lookup;

import java.util.List;

/**
 * Interface for components in the LUT generation framework
 * that have multiple permutations of how components can be
 * ordered.
 */
public interface Permutable extends HasAttributes {
  
  /**
   * Lists all the permutations for this LUT generation component
   * @return List of permutations
   */
  List<ClausePermutation> getPermutations();
}
