package org.mholford.chatlantis.lookup;

import org.mholford.chatlantis.Utils;
import org.mholford.chatlantis.lookup.instruction.Instruction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class to be used by LUTGenerators to create entries for the Lookup
 * dictionary.  It will start from a base Clause and a list of Instructions
 * to be applied.  All permutations of the Clause (incorporating its subelements)
 * will be computed and mapped to the Instructions.  The Instructions are converted
 * into Context DSL statements and concatenated together with semi-colon separator.
 * The result is a one-to-one map of spoken utterance -> instructions to be performed
 * if the utterance matches.  This map is then used to populate the Lookup table.
 */
public class LookupUtil implements Utils {
  
  /**
   * Create map of all legal permutations to concatenated Context DSL statements.  The
   * result is used to populate an FST lookup table.
   * @param sentence Base clause to get permutations of
   * @param instructions Instructions to be applied if this clause matches
   * @return Semi-colon delimited set of Context DSL statements to be performed on match
   */
  public Map<String, String> createLookupEntries(ClauseConf sentence,
                                          List<Instruction> instructions) {
    Map<String, String> output = new HashMap<>();
    
    for (ClausePermutation cp : sentence.getPermutations()) {
      String term = cp.getOutput();
      String value = adaptInstructions(instructions, cp.getMarkerInfo());
      output.put(term, value);
    }
    return output;
  }
  
  private String adaptInstructions(List<Instruction> instructions, List<MarkerConf> markerInfo) {
    int wcCount = 0;
    //int entCount = 0;
    Map<Boolean, List<MarkerConf>> mkrPartition =
        partition(markerInfo, m -> m instanceof WildcardConf);
    List<MarkerConf> wildcards = mkrPartition.get(true);
    List<MarkerConf> entities = mkrPartition.get(false);
    Map<String, List<MarkerConf>> entityMap = new HashMap<>();
    Map<String, Integer> entityCnts = new HashMap<>();
    for (MarkerConf emc : entities) {
      String alias = ((EntityConf) emc).getAlias();
      if (!entityMap.containsKey(alias)) {
        entityMap.put(alias, new ArrayList<>());
      }
      entityMap.get(alias).add(emc);
      entityCnts.put(alias, 0);
    }
    List<String> newInsts = new ArrayList<>();
    for (Instruction inst : instructions) {
      boolean addInst = true;
      String instString = inst.output();
      if (inst.hasWildcard()) {
        WildcardConf wc;
        do {
          if (wcCount >= wildcards.size()) {
            addInst = false;
            break;
          }
          wc = (WildcardConf) wildcards.get(wcCount);
          if (wc.isKeep()) {
            instString = fmt(instString, new Integer(wcCount));
            break;
          }
          wcCount++;
        } while (!wc.isKeep());
      }
      if (inst.hasEntity()) {
        EntityConf ec;
        String alias = inst.getEntity().getAlias();
        do {
          if (!entityCnts.containsKey(alias)) {
            addInst = false;
            break;
          }
          int entCount = entityCnts.get(alias);
          if (entCount >= entityMap.get(alias).size()) {
            addInst = false;
            break;
          }
          ec = (EntityConf) entityMap.get(alias).get(entCount);
          if (ec.isKeep()) {
            instString = fmt(instString, entCount);
            break;
          }
          entityCnts.put(alias, ++entCount);
        } while (!ec.isKeep() && entityCnts.get(alias) < entityMap.get(alias).size());
      }
      if (addInst) {
        newInsts.add(instString);
      }
    }
    return String.join("; ", newInsts);
  }
}
