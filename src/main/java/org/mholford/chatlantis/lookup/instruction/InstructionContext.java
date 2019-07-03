package org.mholford.chatlantis.lookup.instruction;

/**
 * Enumeration representing the various Contexts that
 * exist.  Currently these are <ul>
 *   <li>User</li>
 *   <li>Conversation</li>
 *   <li>Utterance</li>
 * </ul>
 * Also maps these Contexts to the abbreviations used
 * by their context labels.
 */
public enum InstructionContext {
  USER("user"),
  CONVERSATION("conv"),
  UTTERANCE("utt");
  
  private String abbrev;
  
  InstructionContext(String abbrev) {
    this.abbrev = abbrev;
  }
  
  /**
   * Answers the abbreviation used in context label
   * for this Context
   * @return Abbreviation
   */
  public String getAbbrev() {
    return abbrev;
  }
}
