package org.mholford.chatlantis.context;

import org.pcollections.HashTreePMap;
import org.pcollections.PMap;

/**
 * <p>
 * The Context is a data structure used in Chatlantis to represent state.  It is used in
 * a handful of places - Utterances, Conversations and Users each maintain a Context.
 * Context instances are immutable. To change a Context, we effectively create a new copy
 * that includes the changes and return that Context.  We use the pcollections library,
 * which has efficient implementation of these type of "persistent" collections.  A Context
 * holds a hierarchical map of objects keyed by Strings.  This can include arbitrarily
 * deep levels of maps and lists.  All collections within the Context are also immutable.
 * Maps are implemented as instances of Pcollections' HashTreePMap and lists as TreePVector.
 * Contexts "begin" as empty HashTreePMaps keyed by String.</p>
 * <p>
 *   Chatlantis provides a simple DSL to navigate arbitrarily deep hierarchies within
 *   a Context.  There are currently five data types which can be accessed from the Context,
 *   each of which has a "command" character.  These are:
 *   <table>
 *     <tr><td>/</td><td>map</td></tr>
 *     <tr><td>|</td><td>list</td></tr>
 *     <tr><td>.</td><td>string</td></tr>
 *     <tr><td>#</td><td>numeric</td></tr>
 *     <tr><td>?</td><td>boolean</td></tr>
 *   </table>
 *   Commands are read from left-to-right - the command char followed by the key name.
 *   For example:
 *   <table>
 *     <tr><td>/l1/l2.name</td><td>Retrieve map keyed by "l1".  Within that, retrieve
 *     the map keyed by "l2".  Within that, retrieve the string keyed by "name"</td></tr>
 *     <tr><td>/l1|names.3</td><td>Retrieve map keyed by "l1".  Within that, retrieve
 *     the list keyed by "names".  Retrieve the third element of that list</td></tr>
 *   </table>
 *   Navigation of Context according to the syntax above is delegated to the ContextTraversal
 *   class.  Context also provides raw get and puts against the backing map directly.
 *   These methods are _get and _put respectively; they are mostly useful for testing purposes.
 *   </p>
 *   <p>
 *     The Instruction interface provides a higher-level abstraction over context traversal
 *     operations.  Implementations have been create for many common Context manipulations.
 *     These should be used whenever possible.
 *   </p>
 *   <p>
 *     Context should be created usin gthe ContextFactory class.  This is done under the
 *     covers by Chatlantis when new Users, Conversations and Utterances are created.
 *   </p>
 */
public class Context implements ContextConstants {
  private final PMap<String, Object> backingMap;
  private final ContextTraversal contextTraversal = new ContextTraversal();
  
  Context(PMap<String, Object> backingMap) {
    this.backingMap = backingMap;
  }
  
  /**
   * Gets the underlying map for the Context. This will be an instance of HashTreePMap.
   * @return Underlying map.
   */
  public PMap<String, Object> _get() {
    return backingMap;
  }
  
  /**
   * Raw put of value to the underlying PMap.  Mainly useful for testing.
   * @param key Key in base map
   * @param val Value to put
   * @return New Context containing the put
   */
  public Context _put(String key, Object val) {
    PMap<String, Object> pmap = backingMap.plus(key, val);
    return new Context(pmap);
  }
  
  /**
   * Puts the value at the location specified using the Context DSL.  For example,
   * <pre>context.put("/l1/l2.name", "matt")</pre> set the value of name within the l2
   * map (which is within the l1 map) to "matt".
   * @param key Context DSL of where to put the value
   * @param val Value to put
   * @return New Context containing the put
   */
  public Context put(String key, Object val) {
    return contextTraversal.put(backingMap, key, val);
  }
  
  /**
   * Makes a copy of the collection by creating a new Pcollection from the backing map.
   * @return Copy of context
   */
  public Context copy() {
    return new Context(HashTreePMap.from(backingMap));
  }
  
  /**
   * Raw get of value from underlying PMap.  Mainly useful for testing.
   * @param key Key in base map
   * @return Object from base map
   */
  public Object _get(String key) {
    return backingMap.get(key);
  }
  
  /**
   * Gets the value at the location specified using the Context DSL.  For example,
   * <pre>context.get("/l1.name")</pre> will retrieve the value of the "name" property
   * in the map referenced by "l1" in the base map.
   * @param key Context DSL
   * @return Retrieved value
   */
  public Object get(String key) {
    return contextTraversal.get(backingMap, key);
  }
  
  /**
   * Removes the value specifed by Context DSL from the Context.  For example, <pre>
   *   context.remove("/l1/l2")</pre> will remove the entire map reference by "l2" within
   *   the "l1" map.
   * @param key Context DSL specifying path to remove
   * @return New Context incorporating the removal
   */
  public Context remove(String key) {
    return contextTraversal.remove(backingMap, key);
  }
  
  /**
   * Get the size of the underlying map.  This returns the number of elements in the
   * base map only.
   * @return Number of elements in base map
   */
  public int size() {
    return backingMap.size();
  }
  
  /**
   *
   * @param key
   * @return
   */
  public boolean containsKey(String key) {
    return backingMap.containsKey(key);
  }
}
