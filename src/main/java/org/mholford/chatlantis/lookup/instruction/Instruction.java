package org.mholford.chatlantis.lookup.instruction;


import org.mholford.chatlantis.context.FullContext;
import org.mholford.chatlantis.lookup.EntityMarker;

/**
 * Represents a command (or set of commands) to be performed on a FullContext
 * snapshot.  These translate to Context DSL statements which are then processed.
 * Although Instruction class is designed to map a single Context DSL statement,
 * the preExec and postExec hooks can be used to handle more complex operations.
 * These hooks are called immediately before and after the main instruction is
 * executed.
 */
public interface Instruction {
  
  /**
   * Outputs the Context DSL statement which will be executed against the FullContext
   * @return Context DSL statement
   */
  String output();
  
  /**
   * Answers whether the Instruction contains a wildcard to be resolved.  This
   * applies generally to Instructions that are returned from the Lookup table.
   * If there is a wildcard, the Lookup match logic will fill it in from Lookup context.
   * Defaults to false.
   * @return Whether contains a wildcard
   */
  default boolean hasWildcard() {
    return false;
  }
  
  /**
   * Answers whether the Instruction contains an extracted Entity to be resolved.
   * This applies to Instructions that are returned from the Lookup table.  If there
   * is an entity(ies), the Lookup match logic will fill it in from Lookup context.
   * Defaults to false
   * @return Whether contains an Entity
   */
  default boolean hasEntity() {
   return false;
  }
  
  /**
   * Gets the EntityMarker that is contained in the Instruction (if there is one).
   * Returns null by default.
   * @return Entity marker
   */
  default EntityMarker getEntity() {
    return null;
  }
  
  /**
   * Gets which Context this Instructions should be executed against
   * @return Instruction context
   */
  InstructionContext getInstructionContext();
  
  /**
   * Hook to allow manipulation of Context prior to execution of this Instruction.
   * @param fc Context before changes
   * @return Context after changes
   */
  default FullContext preExec(FullContext fc) {return fc;}
  
  /**
   * Hook to allow manipulation of Context after execution of the main Instruction.
   * @param fc Context before changes
   * @return Context after changes
   */
  default FullContext postExec(FullContext fc) {return fc;}
}
