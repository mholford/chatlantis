package org.mholford.chatlantis;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.mholford.chatlantis.context.*;
import org.mholford.chatlantis.lookup.instruction.*;
import org.mholford.fstdict.Range;

import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.mholford.chatlantis.context.SubExpression.CONV;
import static org.mholford.chatlantis.context.SubExpression.UTT;

/**
 * Collection of utility functions and constant objects.  These are intended for use
 * anywhere in the Chatlantis application.  Any class wishing to make use of these
 * functions can do so by simply adding Utils to the list of interfaces it implements.
 */
public interface Utils {
  /**
   * Uses Lucene's StandardAnalyzer as "default"
   */
  Analyzer defaultAnalyzer = new StandardAnalyzer();
  
  /**
   * Uses Lucene's WhitespaceAnalyzer
   */
  Analyzer whitespaceAnalyzer = new WhitespaceAnalyzer();
  
  /**
   * Defines how string input should be tokenized.  The two modes have corresponding
   * Lucene analyzers
   */
  enum TokenizerMode {
    STANDARD(defaultAnalyzer),
    WHITESPACE(whitespaceAnalyzer);
    
    private final Analyzer analyzer;
    
    TokenizerMode(Analyzer analyzer) {
      this.analyzer = analyzer;
    }
  
    /**
     * Gets the appropriate analyze for this mode
     * @return Lucene Analyzer
     */
    public Analyzer getAnalyzer() {
      return analyzer;
    }
  }
  
  /**
   * Shortcut utility method that streams a collection, performs the specified Map and returns
   * them as a list.  This shortens:
   * <code>list.stream().map(mapFn).collect(Collectors.toList())</code>
   * to <code>map(list, mapFn)</code>
   *
   * @param coll   Collection of items we are converting
   * @param mapper The function to convert the items
   * @param <X>    Original type of items
   * @param <Y>    Converted type of items
   * @return List of converted items
   */
  default <X, Y> List<Y> map(Collection<X> coll, Function<X, Y> mapper) {
    return coll.stream().map(mapper).collect(Collectors.toList());
  }
  
  /**
   * Shortcut utility method to filter a collection.  This shortens:
   * <code>list.stream().filter(fn).collect(Collectors.toList())</code> to
   * <code>filter(list, fn)</code>
   *
   * @param coll List of items to filter
   * @param pred Function used to filter the items
   * @param <X>  Type of items in collection
   * @return List of filtered items
   */
  default <X> List<X> filter(Collection<X> coll, Predicate<? super X> pred) {
    return coll.stream().filter(pred).collect(Collectors.toList());
  }
  
  /**
   * Returns the top Entry in a map.  This is defined as the Entry with the highest
   * value when sorted by natural order
   * @param map Map to get top entry from
   * @param <X> Generic type of map key
   * @param <Y> Generic Type of map value
   * @return Entry with highest value
   */
  default <X, Y extends Comparable> Map.Entry<X, Y> topEntry(Map<X, Y> map) {
    Stream<Map.Entry<X, Y>> sorted = map.entrySet().stream()
        .sorted(Map.Entry.<X, Y>comparingByValue().reversed());
    List<Map.Entry<X, Y>> collect = sorted.collect(Collectors.toList());
    return collect.get(0);
  }
  
  /**
   * Shortcut function to create a new Map<String, Object> and populate it inline.
   * The parameter is varargs, but the function will fail if an odd number are given.
   * The values are populated in the obvious way: key,value,key,value....
   *
   * @param vals String key and Object values to put in the map
   * @return Populated map
   */
  default <X, Y> Map<X, Y> mapOf(Object... vals) {
    assert vals.length % 2 == 0;
    Map<X, Y> output = new HashMap<>();
    for (int i = 0; i < vals.length; i += 2) {
      X k = (X) vals[i];
      Y v = (Y) vals[i + 1];
      output.put(k, v);
    }
    return output;
  }
  
  /**
   * Shortcut function to create a new List and populate it inline.
   * @param vals Values for the list
   * @param <X> Generic type of list
   * @return Populated list
   */
  default <X> List<X> listOf(X... vals) {
    return new ArrayList<>(Arrays.asList(vals));
  }
  
  /**
   * Make a shallow copy of the list by creating a new list with the original members
   * @param orig Original list
   * @param <X> Generic type of list
   * @return Shallow copy
   */
  default <X> List<X> copyOf(List<X> orig) {
    return new ArrayList<>(orig);
  }
  
  /**
   * Shortcut method to format a string. Just calls String.format
   * @param orig Original string including placeholders
   * @param params Replacements for placeholders
   * @return Formatted string
   */
  default String fmt(String orig, Object... params) {
    return String.format(orig, params);
  }
  
  /**
   * Splits a string into a list of tokens using the specified Tokenizer
   * @param input original string
   * @param tm Tokenizer to use
   * @return List of tokens
   */
  default List<String> tokenize(String input, TokenizerMode tm) {
    List<String> output = new ArrayList<>();
    Analyzer analyzer = tm.analyzer;
    try {
      TokenStream tstream = analyzer.tokenStream(null, new StringReader(input));
      tstream.reset();
      while (tstream.incrementToken()) {
        output.add(tstream.getAttribute(CharTermAttribute.class).toString());
      }
      tstream.close();
    } catch (IOException e) {
      // Won't happen b/c we're using StringReader and not an IO-based reader
      throw new RuntimeException();
    }
    return output;
  }
  
  /**
   * Shortcut method to get a classpath resource
   * @param path Path to resource
   * @return Input stream of resource
   */
  default InputStream getResource(String path) {
    return Utils.class.getClassLoader().getResourceAsStream(path);
  }
  
  /**
   * Lists all the ways a list of ranges can be permuted.  Impossible range lists
   * (i.e. those containing overlapping ranges are removed)
   * @param input Original range list
   * @return All possible permutations
   */
  default List<List<Range>> getRangePermutations(List<Range> input) {
    List<boolean[]> bools = getBooleanPermutations(input.size());
    List<List<Range>> possible = new ArrayList<>();
    for (boolean[] bool : bools) {
      possible.add(IntStream.range(0, bool.length)
          .filter(i -> bool[i] == true)
          .mapToObj(i -> input.get(i))
          .collect(Collectors.toList()));
    }
    return removeImpossibleRanges(possible);
  }
  
  /**
   * Gets all boolean on/off permutations for a range of size n
   * @param n Size of range
   * @return All on/off permutations
   */
  default List<boolean[]> getBooleanPermutations(int n) {
    return IntStream.range(0, (int) Math.pow(2, n))
        .mapToObj(i -> bitSetToArray(BitSet.valueOf(new long[]{i}), n))
        .collect(toList());
  }
  
  /**
   * Converts a Java BitSet into a boolean array of specified width
   * @param bs Bitset to convert
   * @param width Width of array
   * @return boolean array
   */
  default boolean[] bitSetToArray(BitSet bs, int width) {
    boolean[] result = new boolean[width]; // all false
    bs.stream().forEach(i -> result[i] = true);
    return result;
  }
  
  /**
   * Given a list of list of ranges, removes any list of ranges which is impossible.  A
   * list of ranges is impossible if any of the ranges overlap each other
   * @param inputs List of range lists
   * @return List of range lists with impossible ones removed
   */
  default List<List<Range>> removeImpossibleRanges(List<List<Range>> inputs) {
    List<List<Range>> output = new ArrayList<>();
    for (List<Range> input : inputs) {
      input.sort(Comparator.naturalOrder());
      boolean isOverlap = false;
      for (int i = 0; i < input.size() - 1; i++) {
        List<Range> others = input.subList(i + 1, input.size());
        for (Range other : others) {
          if (rangesOverlap(input.get(i), other)) {
            isOverlap = true;
            break;
          }
        }
        if (isOverlap) {
          break;
        }
      }
      if (!isOverlap) {
        output.add(input);
      }
    }
    
    return output;
  }
  
  /**
   * Answers whether two Ranges overlap.  This is defined as the start of the first
   * range being greater than the end of the second range OR the end of the first
   * range being greater than the start of the second range
   * @param r1 First range
   * @param r2 Second range
   * @return Whether the ranges overlap
   */
  default boolean rangesOverlap(Range r1, Range r2) {
    return r1.getStart() > r2.getEnd() || r1.getEnd() > r2.getStart();
  }
  
  /**
   * Reads the content in Reader into a list of strings.  Each line of content
   * becomes a string in the list.
   * @param r Reader holding content
   * @return List of strings
   * @throws IOException If could not read the content
   */
  default List<String> readLinesIntoList(Reader r) throws IOException {
    List<String> output = new ArrayList<>();
    BufferedReader br = new BufferedReader(r);
    String s;
    while ((s = br.readLine()) != null) {
      output.add(s.trim());
    }
    return output;
  }
  
  /**
   * Shortcut function to create a new Map<String, String> and populate it inline.
   * The parameter is varargs, but the function will fail if an odd number are given.
   * The values are populated in the obvious way: key,value,key,value....
   *
   * @param vals String keys and values to put in the map
   * @return Populated map
   */
  default Map<String, String> stringMapOf(String... vals) {
    assert vals.length % 2 == 0;
    Map<String, String> output = new HashMap<>();
    for (int i = 0; i < vals.length; i += 2) {
      String k = vals[i];
      String v = vals[i + 1];
      output.put(k, v);
    }
    return output;
  }
  
  /**
   * Shortcut utility method to split a collection into two parts.  This shortens:
   * <code>list.stream().collect(Collectors.partioningBy(fn))</code> to
   * <code>partition(list, fn)</code>.
   *
   * @param coll Collection of items to split up
   * @param pred Function used to split the items
   * @param <X>  Type of item in collection
   * @return Map with two entries {true, false} each containing a list of items.  Which list
   * an item ends up in is determined by the result of the partitioning function.
   */
  default <X> Map<Boolean, List<X>> partition(Collection<X> coll, Predicate<? super X> pred) {
    return coll.stream().collect(Collectors.partitioningBy(pred));
  }
  
  /**
   * Takes a semi-colon delimited string of Context DSL statements and converts
   * them to Instructions
   * @param orig Original Context DSL statement
   * @return List of instructions
   */
  default List<Instruction> parseInstructions(String orig) {
    List<Instruction> output = new ArrayList<>();
    
    String[] cmds = orig.split(";");
    for (String cmd : cmds) {
      cmd = cmd.trim();
      SubExpression subEx = getSubEx(cmd);
      InstructionContext instructionContext;
      switch (subEx.getContext()) {
        case UTT:
          instructionContext = InstructionContext.UTTERANCE;
          break;
        case CONV:
          instructionContext = InstructionContext.CONVERSATION;
          break;
        case FullContext.USER:
          instructionContext = InstructionContext.USER;
          break;
        default:
          throw new RuntimeException("Unknown context: " + subEx.getContext());
      }
      
      cmd = subEx.getExpr();
      if (cmd.contains("-!")) {
        String[] ss = cmd.split("-!");
        output.add(new RemoveSlot(instructionContext, ss[1].trim()));
      } else if (cmd.contains("->")) {
        String[] ss = cmd.split("->");
        boolean preFetch = false;
        String ls = ss[0].trim();
        String rs = ss[1].trim();
        if (rs.startsWith("{") && rs.endsWith("}")) {
          preFetch = true;
          rs = rs.substring(1, rs.length() - 1);
        }
        if (ls.equals("/intent.name")) {
          output.add(new SetIntent(instructionContext, rs));
        } else if (ls.equals("/prompt.value")) {
          output.add(new SetPrompt(instructionContext, rs));
        } else {
          output.add(new SetStringSlot(instructionContext, ls, rs, preFetch));
        }
      }
    }
    
    return output;
  }
  
  /**
   * Converts a Context DSL statement into a SubExpression by splitting off the context
   * label.  The resulting SubExpression holds the stripped expression and the Context
   * to which it should be applied.
   * @param orig original Context DSL
   * @return SubExpression containing Context and stripped statement
   */
  default SubExpression getSubEx(String orig) {
    if (!orig.startsWith("$")) {
      throw new ContextTraversalException("Dereferencing FullContext requires a $VOL: to specify " +
          "which sub context");
    }
    int colonIdx = orig.indexOf(':');
    String ctxName = orig.substring(1, colonIdx);
    String subEx = orig.substring(colonIdx + 1);
    return new SubExpression(ctxName, subEx);
  }
  
  default void printContext(Context ctx) throws IOException {
    ContextPrinter.get().printContext(ctx);
  }
  
  default String getPrintedContext(Context ctx) throws IOException {
    return ContextPrinter.get().getPrintedContextString(ctx);
  }
}
