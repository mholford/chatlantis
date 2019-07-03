package org.mholford.chatlantis.lookup;

import java.util.*;

/**
 * Represents a single permutation of a ClauseConf.  It is composed of the regular
 * text output and list of Marker (wildcard or entity) configurations.  The marker
 * information is used to capture dynamic elements in a Lookup sentence so that
 * they can be used in the resulting Instructions.  ClausePermutation is an effectively
 * immutable class: all member fields are final.
 */
public class ClausePermutation {
  private final String output;
  private final List<MarkerConf> markerInfo;
  
  /**
   * Creates a permutation with the specified literal output and list of marker info
   * @param output Literal output
   * @param markerInfo Marker info
   */
  public ClausePermutation(String output, List<MarkerConf> markerInfo) {
    this.output = output;
    this.markerInfo = markerInfo;
  }
  
  /**
   * Creates a permutation with the specified literal output
   * @param output Literal output
   */
  public ClausePermutation(String output) {
    this(output, new ArrayList<>());
  }
  
  /**
   * Gets the literal output of this permutation
   * @return Literal output
   */
  public String getOutput() {
    return output;
  }
  
  /**
   * Gets the marker information for this permutation
   * @return Marker information
   */
  public List<MarkerConf> getMarkerInfo() {
    return markerInfo;
  }
  
  /**
   * Creates "deep" clone of marker info list.  The cloned list contains
   * cloned instances of the WildcardConf and EntityConf elements within.
   * @return Deep clone of marker info list
   */
  public List<MarkerConf> cloneMarkerInfo() {
    List<MarkerConf> output = new ArrayList<>();
    for (MarkerConf mc : markerInfo) {
      if (mc instanceof WildcardConf) {
        output.add(new WildcardConf(mc.isKeep()));
      } else { /*mc is EntityConf*/
        output.add(new EntityConf(((EntityConf)mc).getAlias(), mc.isKeep(),
            mc.cloneAttributes()));
      }
    }
    return output;
  }
  
  /**
   * Answers whether this permutation has any marker info (wildcards or entities)
   * @return Whether has markers
   */
  public boolean hasMarkers() {
    return markerInfo.size() > 0;
  }
}
