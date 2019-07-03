package org.mholford.chatlantis.lookup.instruction;

import org.mholford.chatlantis.Utils;
import org.mholford.chatlantis.lookup.EntityMarker;
import org.mholford.chatlantis.lookup.Marker;
import org.mholford.chatlantis.lookup.WildcardMarker;

/**
 * Instruction to set a slot in Context.  This class is genericized to allow different
 * value types.  Current implementations are for Boolean, String and Numeric slots.  Use
 * those subclasses directly.  This instruction also supports setting the slot to the
 * value of a Marker (Entity or Wildcard).  The value to assign can be pre-fetched from
 * Context by setting prefetchValue=true.
 * @param <T> Type of Slot
 */
public abstract class SetSlot<T> implements Instruction, Utils {
  private final InstructionContext instructionContext;
  private final String slotName;
  private final T slotValue;
  private final Marker slotValueMarker;
  private final boolean prefetchValue;
  
  /**
   * Sets the specified slot to specified value in specified context, optionally prefetching
   * the value from Context.
   * @param instructionContext Which Context
   * @param slotName Which slot
   * @param slotValue What to set slot to
   * @param prefetchValue Whether to prefetch value from Context
   */
  public SetSlot(InstructionContext instructionContext, String slotName, T slotValue,
                 boolean prefetchValue) {
    this.instructionContext = instructionContext;
    this.slotName = slotName;
    this.slotValue = slotValue;
    this.prefetchValue = prefetchValue;
    slotValueMarker = null;
  }
  
  /**
   * Sets the specified slot to the specified value in the specified Context without prefetching
   * the value from Context.
   * @param instructionContext Which Context
   * @param slotName Which slot
   * @param slotValue What to set slot to
   */
  public SetSlot(InstructionContext instructionContext, String slotName, T slotValue) {
    this(instructionContext, slotName, slotValue, false);
  }
  
  /**
   * Sets the specified slot to the value of the specified Marker in the specified Contxt,
   * optionally prefetching value from Context
   * @param instructionContext Which Context
   * @param slotName Which slot
   * @param slotValueMarker Marker to use value of
   * @param prefetchValue Whether to prefetch value from Context
   */
  public SetSlot(InstructionContext instructionContext, String slotName, Marker slotValueMarker,
                 boolean prefetchValue) {
    this.instructionContext = instructionContext;
    this.slotName = slotName;
    this.slotValueMarker = slotValueMarker;
    this.prefetchValue = prefetchValue;
    slotValue = null;
  }
  
  /**
   * Sets the specified slot to the value of the specified Marker in the specified Context,
   * without prefetching value from Context.
   * @param instructionContext Which Context
   * @param slotName Which slot
   * @param slotValueMarker Marker to use value of
   */
  public SetSlot(InstructionContext instructionContext, String slotName, Marker slotValueMarker) {
    this(instructionContext, slotName, slotValueMarker, false);
  }
  
  /**
   * Sets the specified slot to the specified value in Utterance Context.  The value will not
   * be prefetched
   * @param slotName Which slot
   * @param slotValue What to set slot to
   */
  public SetSlot(String slotName, T slotValue) {
    this(InstructionContext.UTTERANCE, slotName, slotValue, false);
  }
  
  /**
   * Sets the specified slot to the value of the specified marker. The value will not be prefetched
   * @param slotName Which slot
   * @param slotValueMarker Marker to use value of
   */
  public SetSlot(String slotName, Marker slotValueMarker) {
    this(InstructionContext.UTTERANCE, slotName, slotValueMarker, false);
  }
  
  @Override
  public String output() {
    Object slotOutputVal = null;
    if (slotValue != null) {
      slotOutputVal = slotValue;
    } else if (slotValueMarker != null) {
      if (slotValueMarker instanceof WildcardMarker) {
        slotOutputVal = "WC_%d";
      } else if (slotValueMarker instanceof EntityMarker) {
        EntityMarker em = (EntityMarker) slotValueMarker;
        slotOutputVal = em.getAlias() + "_MKR_%d";
      }
    }
    if (slotOutputVal == null) {
      throw new RuntimeException("Unknown value to set slot");
    }
    if (!prefetchValue) {
      return fmt("$%s:%s -> %s", instructionContext.getAbbrev(), slotName, slotOutputVal);
    } else {
      return fmt("$%s:%s -> {%s}", instructionContext.getAbbrev(), slotName, slotOutputVal);
    }
  }
  
  @Override
  public boolean hasWildcard() {
    return slotValueMarker != null && slotValueMarker instanceof WildcardMarker;
  }
  
  @Override
  public boolean hasEntity() {
    return slotValueMarker != null && slotValueMarker instanceof EntityMarker;
  }
  
  @Override
  public EntityMarker getEntity() {
    return hasEntity() ? (EntityMarker) slotValueMarker : null;
  }
  
  @Override
  public InstructionContext getInstructionContext() {
    return instructionContext;
  }
}
