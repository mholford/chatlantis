package org.mholford.chatlantis.workflow;

import org.mholford.chatlantis.Utils;
import org.mholford.chatlantis.utterance.*;
import org.mholford.fstdict.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Performs Entity Extraction on an Utterance using a specified dictionary.  When processing
 * the utterance, it will create two permutations for each entity extracted.  The first will
 * replace the literal token in the utterance with an Entity token (using the specified
 * alias).  The second will retain the matched literal string as is.  This allows multiple
 * interpretations of an utterance to match and is useful in resolving ambiguities.
 */
public class EEUtteranceProcessor implements UtteranceProcessor, Utils {
  private String dictFile;
  private String alias;
  private FSTDictionaryManager dm;
  private final TokenFactory tf = TokenFactory.get();
  private final UtteranceFactory uf = UtteranceFactory.get();
  public static final String DICT_FILE_PROP = "dict";
  public static final String ALIAS_PROP = "alias";
  
  
  public EEUtteranceProcessor() {
  }
  
  @Override
  public List<Utterance> process(Utterance input) throws IOException {
    List<Utterance> output = new ArrayList<>();
    List<LiteralTokenSpan> ltss = input.findLiteralTokenSpans();
    Map<Integer, Range> refRanges = new HashMap<>();
    Map<Range, EntityInfo> infos = new HashMap<>();
    
    for (LiteralTokenSpan lts : ltss) {
      List<EntityAnnotation> entities = dm.findEntities(lts.asString(), true, true);
      // Translate range coordinates to phrase location
      for (EntityAnnotation ea : entities) {
        Range r = ea.getRange();
        Range n = new Range(r.getStart() + lts.getStart(), r.getEnd() + lts.getStart());
        refRanges.put(n.getStart(), n);
        infos.put(n, ea.getEntityInfo());
      }
    }
    
    List<Range> pre = new ArrayList<>(infos.keySet());
    List<List<Range>> rangePermutations = getRangePermutations(pre);
    
    for (List<Range> ranges : rangePermutations) {
      int i = 0;
      List<Token> tokens = new ArrayList<>();
      while (i < input.size()) {
        Token tok = null;
        if (refRanges.containsKey(i) && ranges.contains(refRanges.get(i))) {
          EntityInfo entity = infos.get(refRanges.get(i));
          
          // We use the term as there are no payloads
          tok = tf.createEntity(alias, entity.getTerm());
          i += refRanges.get(i).size();
        } else {
          Token t = input.getToken(i);
          if (t instanceof EntityToken) {
            EntityToken et = (EntityToken) t;
            tok = tf.createEntity(et.getAlias(), et.getValue());
          } else {
            tok = tf.createLiteral(input.getToken(i).getValue());
          }
          i++;
        }
        if (tok != null) {
          tokens.add(tok);
        }
      }
      Utterance u = uf.create(tokens, input.getConversation());
      output.add(u);
    }
    
    return output;
  }
  
  @Override
  public void init(Map<String, String> props) throws IOException {
    dictFile = props.get(DICT_FILE_PROP);
    alias = props.get(ALIAS_PROP);
    InputStream dictIn = getResource(dictFile);
    dm = FSTDictionaryManagerFactory.get().createDefault();
    dm.loadDictionary(dictIn);
  }
  
  /**
   * Gets the path to the dictionary file used for Entity Extraction
   * @return Path
   */
  public String getDictFile() {
    return dictFile;
  }
  
  /**
   * Gets the alias to use in creating Entity Markers
   * @return Alias
   */
  public String getAlias() {
    return alias;
  }
}
