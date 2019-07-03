package org.mholford.chatlantis.context;

import org.mholford.chatlantis.Utils;
import org.pcollections.PMap;
import org.pcollections.PSequence;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Prints a helpful tree diagram showing what is in a particular Context.
 * Adapted from https://stackoverflow.com/a/8948691 .  The output is intended to resemble
 * that of the linux tree command.
 */
public class ContextPrinter implements Utils {
  private static final String TAIL = "└── ";
  private static final String BRANCH = "├── ";
  private static final String SPACE = "    ";
  private static final String SPINE = "│   ";
  private static ContextPrinter INSTANCE;
  
  private ContextPrinter() {}
  
  public static ContextPrinter get() {
    if (INSTANCE == null) {
      INSTANCE = new ContextPrinter();
    }
    return INSTANCE;
  }
  
  private static String branch(boolean isTail) {
    return isTail ? TAIL : BRANCH;
  }
  
  private static String space(boolean isTail) {
    return isTail ? SPACE : SPINE;
  }
  
  /**
   * Prints a tree diagram of specified Context
   * @param ctx Context
   */
  public void printContext(Context ctx) throws IOException {
    String output = getPrintedContextString(ctx);
    System.out.println(output);
  }
  
  public String getPrintedContextString(Context ctx) throws IOException {
    StringWriter writer = new StringWriter();
    PMap<String, Object> pmap = ctx._get();
    getPrintedContextString(pmap, "", "ROOT", true, writer);
    return writer.toString();
  }
  
  private void getPrintedContextString(Object o, String prefix, String name, boolean isTail,
                                       Writer writer) throws IOException {
    String suffix = "";
    if (!(o instanceof Collection) && !(o instanceof Map)) {
      suffix = " = " + String.valueOf(o);
    }
    writer.write(fmt("%s%s%s%s\n", prefix, branch(isTail), name, suffix));
    if (o instanceof PMap) {
      PMap pmap = (PMap) o;
      List entryList = new ArrayList(pmap.entrySet());
      for (int i = 0; i < entryList.size() - 1; i++) {
        Map.Entry me = (Map.Entry) entryList.get(i);
        String key = (String) me.getKey();
        getPrintedContextString(me.getValue(), prefix + space(isTail), key, false, writer);
      }
      if (entryList.size() > 0) {
        Map.Entry me = (Map.Entry) entryList.get(entryList.size() - 1);
        String key = (String) me.getKey();
        getPrintedContextString(me.getValue(), prefix + space(isTail), key, true, writer);
      }
    } else if (o instanceof PSequence) {
      PSequence plist = (PSequence) o;
      for (int i = 0; i < plist.size() - 1; i++) {
        getPrintedContextString(plist.get(i), prefix + space(isTail), String.valueOf(i), false, writer);
      }
      if (plist.size() > 0) {
        int lastIdx = plist.size()-1;
        getPrintedContextString(plist.get(lastIdx), prefix + space(isTail), String.valueOf(lastIdx),
            true, writer);
      }
    }
  }
}
