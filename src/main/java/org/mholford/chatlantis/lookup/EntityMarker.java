package org.mholford.chatlantis.lookup;

/**
 * Represents an Entity placeholder to be used in Instructions.  The only
 * property is the alias which should be used in place of the Entity in
 * Instruction payloads.  This class is effectively immutable.
 */
public class EntityMarker implements Marker {
  private final String alias;
  
  /**
   * Creates a new Entity marker with the specified alias
   * @param alias Alias for the entity
   */
  public EntityMarker(String alias) {
    this.alias = alias;
  }
  
  /**
   * Gets the alias used for this Entity
   * @return Alias for entity
   */
  public String getAlias() {
    return alias;
  }
}
