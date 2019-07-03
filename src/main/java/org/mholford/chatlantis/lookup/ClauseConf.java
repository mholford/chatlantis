package org.mholford.chatlantis.lookup;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a clause in a sentence structure which can contains various sub-elements.
 * These may include phrases, entities, wildcards and other clauses.  Clauses have a name
 * and a set of attributes associated with them.
 * <p>
 *   ClauseConf is an immutable class.  There are methods to add sub-elements, though.  These
 *   methods return a new ClauseConf that incorporates the addition.  This also means one can
 *   use a "fluent" style in configuring a ClauseConf.
 * </p>
 */
public class ClauseConf implements Permutable {
  private final List<Permutable> elements;
  private final String name;
  private final EnumSet<GenAttr> attributes;
  
  /**
   * Creates a new ClauseConf with the specified name, subelements and attributes.
   * @param name Name of the clause
   * @param elements list of subelements
   * @param attributes Attributes of this clause
   */
  public ClauseConf(String name, List<Permutable> elements, EnumSet<GenAttr> attributes) {
    this.name = name;
    this.elements = elements;
    this.attributes = attributes;
  }
  
  /**
   * Create a new ClauseConf with the specified name and attributes.  It is initialized
   * with no subelements.
   * @param name Name of the clause
   * @param attrs Attributes of the clause
   */
  public ClauseConf(String name, EnumSet<GenAttr> attrs) {
    this(name, new ArrayList<>(), attrs);
  }
  
  /**
   * Create a new ClauseConf with the specified name.  It will has no attributes and no
   * sub-elements
   * @param name Name of the clause
   */
  public ClauseConf(String name){
    this(name, EnumSet.noneOf(GenAttr.class));
  }
  
  /**
   * Add a Clause having the specified name, subelements and attributes to the current clause.
   * @param name Name of the new clause
   * @param subelements Subelements of the new clause
   * @param attrs Attributes of the new clause
   * @return Original clause with new clause added
   */
  public ClauseConf addSubClause(String name, List<Permutable> subelements, EnumSet<GenAttr> attrs) {
    ClauseConf newClause = new ClauseConf(name, subelements, attrs);
    elements.add(newClause);
    return this;
  }
  
  /**
   * Add the specified clause to the current clause with the specified attributes.
   *
   * @param clause Clause to add
   * @param attrs Attributes for the new clause
   * @return Original clause with new clause added
   */
  public ClauseConf addSubClause(ClauseConf clause, EnumSet<GenAttr> attrs) {
    return addSubClause(clause.getName(), clause.getElements(), attrs);
  }
  
  /**
   * Add the specified clause to the current clause with the specified attributes.
   * @param clause Clause to add
   * @param attrs Attributes for the new clause
   * @return Original clause with new clause added
   */
  public ClauseConf addSubClause(ClauseConf clause, GenAttr... attrs) {
    return addSubClause(clause, EnumSet.of(attrs[0], attrs));
  }
  
  /**
   * Adds a phrase with the specified name, variants and attributes to the current clause.
   * @param name Name of the new phrase
   * @param variants Variants of the new phrase
   * @param attrs Attributes for the new phrase
   * @return Original Clause with the phrase added
   */
  public ClauseConf addPhrase(String name, List<String> variants, EnumSet<GenAttr> attrs) {
    PhraseConf newPhrase = new PhraseConf(name, variants, attrs);
    elements.add(newPhrase);
    return this;
  }
  
  /**
   * Adds a phrase with the specified name, variants and attributes to the current clause.
   * @param name Name of the new phrase
   * @param variants Variants of the new phrase
   * @param attrs Attributes for the new phrase
   * @return Original Clause with the phrase added
   */
  public ClauseConf addPhrase(String name, List<String> variants, GenAttr... attrs) {
    return addPhrase(name, variants, EnumSet.of(attrs[0], attrs));
  }
  
  /**
   * Adds a phrase with the specified name and variants to the current clause.  This new phrase
   * will have no attributes.
   * @param name Name of the new phrase
   * @param variants Variants of the new phrase
   * @return Original clause with the phrase added
   */
  public ClauseConf addPhrase(String name, List<String> variants) {
    return addPhrase(name, variants, EnumSet.noneOf(GenAttr.class));
  }
  
  /**
   * Adds a wildcard with the specified attributes and the option to keep what the wildcard
   * replaces (so it can be part of the Lookup result).
   * @param keep Whether to keep what the wildcard replaces
   * @param attrs Attributes of the wildcard
   * @return original clause with the wildcard added
   */
  public ClauseConf addWildcard(boolean keep, EnumSet<GenAttr> attrs) {
    WildcardConf newWc = new WildcardConf(keep, attrs);
    elements.add(newWc);
    return this;
  }
  
  /**
   * Adds a wildcard with the option to keep what the wildcard replaces.
   * @param keep Whether to keep what the wildcard replaces
   * @return Original clause with the wildcard added
   */
  public ClauseConf addWildcard(boolean keep) {
    return addWildcard(keep, EnumSet.noneOf(GenAttr.class));
  }
  
  /**
   * Adds a wildcard with the specified attributes and the option to keep what the wildcard
   * replaces (so it can be part of the Lookup result).
   * @param keep Whether to keep what the wildcard replaces
   * @param attrs Attributes of the wildcard
   * @return original clause with the wildcard added
   */
  public ClauseConf addWildcard(boolean keep, GenAttr... attrs) {
    return addWildcard(keep, EnumSet.of(attrs[0], attrs));
  }
  
  /**
   * Adds an Entity marker with the specified alias and attributes and the option to keep
   * what the enity marker replaces (so it can be part of the Lookup result).
   * @param alias Alias for the marker
   * @param keep Whether to keep what the marker replaces
   * @param attrs Attributes for the entity
   * @return Original clause with the entity marker added
   */
  public ClauseConf addEntity(String alias, boolean keep, EnumSet<GenAttr> attrs) {
    EntityConf newEc = new EntityConf(alias, keep, attrs);
    elements.add(newEc);
    return this;
  }
  
  /**
   * Adds an Entity marker with the specified alias and attributes and the option to keep
   * what the enity marker replaces (so it can be part of the Lookup result).
   * @param alias Alias for the marker
   * @param keep Whether to keep what the marker replaces
   * @param attrs Attributes for the entity
   * @return Original clause with the entity marker added
   */
  public ClauseConf addEntity(String alias, boolean keep, GenAttr... attrs) {
    return addEntity(alias, keep, EnumSet.of(attrs[0], attrs));
  }
  
  /**
   * Adds an Entity marker with the specified alias the option to keep what the enity marker
   * replaces (so it can be part of the Lookup result).  The new Entity marker will have no
   * attributes
   * @param alias Alias for the marker
   * @param keep whether to keep what the marker replaces
   * @return Original clause with the entity marker added
   */
  public ClauseConf addEntity(String alias, boolean keep) {
    return addEntity(alias, keep, EnumSet.noneOf(GenAttr.class));
  }
  
  @Override
  public EnumSet<GenAttr> getAttributes() {
    return attributes;
  }
  
  @Override
  public List<ClausePermutation> getPermutations() {
    List<ClausePermutation> perms = new ArrayList<>();
    perms.add(new ClausePermutation(""));
    Iterator<Permutable> elemIter = elements.iterator();
    while (elemIter.hasNext()) {
      Permutable elem = elemIter.next();
      List<ClausePermutation> addPerms = elem.getPermutations();
      List<ClausePermutation> newPerms = new ArrayList<>();
      for (ClausePermutation cp : perms) {
        if (elem.hasAttribute(GenAttr.OPTIONAL)) {
          newPerms.add(new ClausePermutation(cp.getOutput(), cp.getMarkerInfo()));
        }
        for (ClausePermutation cap : addPerms) {
          List<MarkerConf> markerInfo = cp.cloneMarkerInfo();
          if (cap.hasMarkers()) {
            markerInfo.addAll(cap.getMarkerInfo());
          }
          String space = cp.getOutput().length() > 0 ? " " : "";
          newPerms.add(new ClausePermutation(cp.getOutput() + space + cap.getOutput(), markerInfo));
        }
      }
      perms = newPerms;
    }
    return perms;
  }
  
  /**
   * Gets the name of this clause
   * @return Name of clause
   */
  public String getName() {
    return name;
  }
  
  /**
   * Gets the list of subelements for this clause
   * @return List of subelements
   */
  public List<Permutable> getElements() {
    return elements;
  }
}
