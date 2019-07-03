package org.mholford.chatlantis.lookup;

import com.google.common.base.Strings;
import org.mholford.chatlantis.Utils;
import org.mholford.chatlantis.utterance.EntityToken;
import org.mholford.chatlantis.utterance.LiteralToken;
import org.mholford.chatlantis.utterance.Token;
import org.mholford.chatlantis.utterance.Utterance;
import org.mholford.fstdict.EntityInfo;
import org.mholford.fstdict.FSTDictionaryManager;
import org.mholford.fstdict.FSTDictionaryManagerFactory;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a LookupTable backed by an FST dictionary.  We use lucene's FST
 * library.  The lookup tokenizes the original utterance and steps through the
 * lookup word by word.  If a word is not found, we try replacing with a wildcard.
 * The full algorithm is more complex and incorporates backchecking in the event
 * wildcards replace multiple words.  We keep a record of Wildcards and Entity markers
 * encountered in the lookup utterance.  These are then incorporated into the resulting
 * Context DSL statements, which are then returned.
 */
public class FSTLookupTable implements LookupTable, Utils {
  private FSTDictionaryManager dm;
  private final Pattern WC_PATTERN = Pattern.compile("WC_(\\d*)$");
  private final Pattern MKR_PATTERN = Pattern.compile("(.*)_MKR_(\\d*)$");
  final static String DICT_PARAM = "dict";
  final static String GENERATOR_PARAM = "generator";
  
  /**
   * Creates a new FSTLookup table
   */
  public FSTLookupTable() {
    dm = FSTDictionaryManagerFactory.get().createDefault();
  }
  
  @Override
  public String lookup(Utterance u) throws IOException {
    List<String> wildcards = new ArrayList<>();
    Map<String, List<String>> entities = new HashMap<>();
    List<String> soFar = new ArrayList<>();
    List<String> luString = new ArrayList<>();
    boolean matches = true;
    List<Token> tokens = map(u.getTokenRanges(), tr -> tr.getToken());
    Iterator<Token> tokIter = tokens.iterator();
    
    while (tokIter.hasNext()) {
      Token tok = tokIter.next();
      if (tok instanceof LiteralToken) {
        soFar.add(tok.getValue());
        if (dm.hasTokens(soFar)) {
          luString = copyOf(soFar);
        } else {
          List<String> wcVal = new ArrayList<>();
          
          wcVal.add(0, soFar.get(soFar.size()-1));
          soFar = soFar.subList(0, soFar.size()-1);
          soFar.add("*");
          while (!dm.hasTokens(soFar)) {
            // try putting the * further back...
            wcVal.add(0, soFar.get(soFar.size() - 2));
            soFar = soFar.subList(0, soFar.size() - 2);
            soFar.add("*");
          }
          
          StringBuilder wildcard = new StringBuilder(String.join(" ", wcVal));
          boolean addWc = true;
          while (tokIter.hasNext()) {
            tok = tokIter.next();
            if (tok instanceof LiteralToken) {
              soFar.add(tok.getValue());
              if (!dm.hasTokens(soFar)) {
                wildcard.append(" " + tok.getValue());
                soFar = soFar.subList(0, soFar.size() - 1);
              } else {
                wildcards.add(wildcard.toString());
                addWc = false;
                break;
              }
            } else if (tok instanceof EntityToken) {
              EntityToken etok = (EntityToken) tok;
              String alias = etok.getAlias();
              if (!entities.containsKey(alias)) {
                entities.put(alias, new ArrayList<>());
              }
              entities.get(alias).add(tok.getValue());
              soFar.add(etok.toLookupString());
              break;
            }
          }
          if (dm.hasTokens(soFar)) {
            if (addWc) {
              wildcards.add(wildcard.toString());
            }
            luString = copyOf(soFar);
          } else {
            matches = false;
          }
        }
      } else if (tok instanceof EntityToken) {
        EntityToken etok = (EntityToken) tok;
        String alias = etok.getAlias();
        if (!entities.containsKey(alias)) {
          entities.put(alias, new ArrayList<>());
        }
        entities.get(alias).add(tok.getValue());
        soFar.add(etok.toLookupString());
        if (dm.hasTokens(soFar)) {
          luString = copyOf(soFar);
        } else {
          matches = false;
        }
      }
    }
    
    if (!matches || !dm.isCompleteMatch(luString)) {
      return null;
    }
    EntityInfo ei = dm.getEntity(luString);
    String payload = ei.getPayloads().get(0);
    return resolvePayload(payload, wildcards, entities);
  }
  
  private String resolvePayload(String payload, List<String> wildcards,
                                Map<String, List<String>> entities) {
    List<String> payloadTokens = tokenize(payload, TokenizerMode.WHITESPACE);
    List<String> output = new ArrayList<>();
    
    for (String tok : payloadTokens) {
      // if ends with ;, strip it and replace later
      boolean endsWithSemi = tok.endsWith(";");
      String curr = endsWithSemi ? tok.substring(0, tok.length() - 1) : tok;
      String ending = endsWithSemi ? ";" : "";
      // extract number from e.g. WC_0 and replace with wildcards.get(0)
      Matcher wcMatcher = WC_PATTERN.matcher(curr);
      Matcher entMatcher = MKR_PATTERN.matcher(curr);
      if (wcMatcher.matches()) {
        String wcIdxString = wcMatcher.group(1);
        int wcIdx = Integer.parseInt(wcIdxString);
        String wcValue = wildcards.get(wcIdx);
        output.add(wcValue + ending);
      } else if (entMatcher.matches()) {
        // extract alias and number from e.g. EMP_MKR_0 and replace with entities.get(EMP).get(0)
        String alias = entMatcher.group(1);
        String idxString = entMatcher.group(2);
        int idx = Integer.parseInt(idxString);
        String entValue = entities.get(alias).get(idx);
        output.add(entValue + ending);
      } else {
        output.add(tok);
      }
    }
    
    return String.join(" ", output);
  }
  
  /**
   * Initializes the FST lookup table from specified properties.  Creates a new instance
   * of the LUTGenerator specified in the properties; runs that generator and loads the
   * generated entries into an FST-based dictionary.
   * @param props Property map
   * @throws IOException If something went wrong
   */
  public void init(Map<String, String> props) throws IOException {
    String dict = props.get(DICT_PARAM);
    String generatorCls = props.get(GENERATOR_PARAM);
    if (!Strings.isNullOrEmpty(generatorCls)) {
      try {
        Class<LUTGenerator> lgc = (Class<LUTGenerator>) Class.forName(generatorCls);
        LUTGenerator lg = lgc.newInstance();
        lg.generate();
      } catch (ReflectiveOperationException e) {
        throw new IOException("Could not instatiate LUT generator: " + generatorCls, e);
      }
    }
    dm.loadDictionary(getResource(dict));
  }
}
