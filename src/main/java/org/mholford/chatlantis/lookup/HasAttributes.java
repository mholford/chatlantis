package org.mholford.chatlantis.lookup;

import java.util.EnumSet;

/**
 * Interface for LUT generation components which support
 * one or more of the GenAttr attributes.  Provides
 * convenience methods to get component's attributes
 * and answer whether it a component has a particular
 * attribute.
 */
public interface HasAttributes {
  /**
   * Get attributes associated with this component
   * @return Set of attributes
   */
  EnumSet<GenAttr> getAttributes();
  
  /**
   * Answer whether the specified attribute is present
   * for this component.
   * @param attr The attribute
   * @return Whether it the attribute is present
   */
  default boolean hasAttribute(GenAttr attr) {
    return getAttributes().contains(attr);
  }
}
