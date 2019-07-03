package org.mholford.chatlantis.lookup;

import org.mholford.chatlantis.Utils;

import java.util.EnumSet;
import java.util.List;

/**
 * Represents a placeholder in a sentence for an extracted entity.  It is composed
 * of an alias for the entity and whether the value the entity marker replaces should
 * be retained (so it can be incorporated in the Lookup result).  Entity markers are
 * converted to XXX_MKR (where XXX is the value of the alias field) when permutations
 * of the clause containing the entity are generated.
 */
public class EntityConf extends MarkerConf implements Utils {
  private final String alias;
  
  /**
   * Creates an Entity placeholder having the specified name, retention policy and
   * attributes.
   * @param alias alias for the entity marker
   * @param keep Whether to keep what the entity replaces
   * @param attrs Attributes of the Entity placeholder
   */
  public EntityConf(String alias, boolean keep, EnumSet<GenAttr> attrs) {
    super(keep, attrs);
    this.alias = alias;
  }
  @Override
  public List<ClausePermutation> getPermutations() {
    return listOf(new ClausePermutation(alias + "_MKR", listOf(this)));
  }
  
  /**
   * Gets the alias for the entity
   * @return Alias
   */
  public String getAlias() {
    return alias;
  }
}
