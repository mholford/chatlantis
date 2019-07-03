package org.mholford.chatlantis.utterance;

import org.mholford.chatlantis.Utils;

/**
 * Represents a token that holds an extracted entity.  When the token
 * is converted to a lookup string, the token alias + "_MKR" is used.
 * This allows the LUT to support large numbers of entities without
 * the need for additional entries.  This marker can be replaced later
 * by the actual extracted value.  This class is effectively immutable.
 * New instances should be created by calling TokenFactory.get().createEntityToken().
 */
public class EntityToken implements Token, Utils {
  private final String alias;
  private final String value;
  
  EntityToken(String alias, String value) {
    this.alias = alias;
    this.value = value;
  }
  
  @Override
  public String getValue() {
    return value;
  }
  
  @Override
  public String toLookupString() {
    return alias + "_MKR";
  }
  
  /**
   * Gets the alias for this type of Entity
   * @return Alias
   */
  public String getAlias() {
    return alias;
  }
  
  @Override
  public String toString() {
    return fmt("%s(%s)", alias, value);
  }
}
