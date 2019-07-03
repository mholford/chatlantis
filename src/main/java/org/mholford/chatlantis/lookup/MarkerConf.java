package org.mholford.chatlantis.lookup;

import java.util.EnumSet;

/**
 * Sentence generation element representing a dynamic "marker" element
 * whose value gets filled in.  Examples are Wildcards and extracted
 * entites.  The Marker has the option of keeping the replaced value
 * possibly for use in the resulting payload.  It also maintains a set
 * of GenAttr attributes.
 */
public abstract class MarkerConf implements Permutable {
  private final boolean keep;
  private final EnumSet<GenAttr> attrs;
  
  /**
   * Creates a MarkerConf with the specified attributes and retention policy
   * @param keep Whether to keep the value that replaces the marker
   * @param attrs Set of Attributes
   */
  public MarkerConf(boolean keep, EnumSet<GenAttr> attrs) {
    this.keep = keep;
    this.attrs = attrs;
  }
  
  /**
   * Answers whether we should keep the value that replace the marker
   * @return Whether
   */
  public boolean isKeep() {
    return keep;
  }
  
  @Override
  public EnumSet<GenAttr> getAttributes() {
    return attrs;
  }
  
  /**
   * Clones the attribute set.  As these are just enums, a shallow clone is
   * sufficient
   * @return Cloned attribute set
   */
  public EnumSet<GenAttr> cloneAttributes() {
    return attrs.clone();
  }
}
