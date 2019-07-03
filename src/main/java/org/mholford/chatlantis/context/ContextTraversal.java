package org.mholford.chatlantis.context;

import org.mholford.chatlantis.Utils;
import org.pcollections.PMap;
import org.pcollections.PSequence;

import java.util.*;

/**
 * Helper class that handles traversing a Context with the Context DSL.  Context
 * delegates it's traversal commands to an instance of this class.
 * <p>
 *   Internally, the class splits the Context DSL string into a list of instructions
 *   and then follows those instructions from left to right.  As it progresses through
 *   the instructions, it keeps a pointer of where it is in the Context map.  Because
 *   all the nested maps and lists are immutable, we need to roll up new instances
 *   of them when we return a new object.  Thus the Traversal class needs to keep a
 *   stack of updates to propogate changes up the hierarchy.
 * </p>
 */
public class ContextTraversal implements ContextConstants, Utils {
  
  private enum Symbol {
    SLASH(ContextConstants.SLASH),
    PIPE(ContextConstants.PIPE),
    DOT(ContextConstants.DOT),
    HASH(ContextConstants.HASH),
    QM(ContextConstants.QM);
    
    private final char repr;
    
    Symbol(char repr) {
      this.repr = repr;
    }
  }
  
  private class Instruction {
    private final Symbol symbol;
    private final String payload;
    
    Instruction(Symbol symbol, String payload) {
      this.symbol = symbol;
      this.payload = payload;
    }
  }
  
  private interface Update {
    Object doUpdate(Object parent, Object latest);
    
    Object getRef();
  }
  
  private class ListUpdate implements Update {
    final int idx;
    final Object ref;
    
    ListUpdate(int idx, Object ref) {
      this.idx = idx;
      this.ref = ref;
    }
    
    @Override
    public PSequence doUpdate(Object parent, Object latest) {
      PSequence output = (PSequence) parent;
      if (output.size() > idx) {
        output = output.minus(idx);
      }
      output = output.plus(latest);
      return output;
    }
    
    @Override
    public Object getRef() {
      return ref;
    }
  }
  
  private class MapUpdate implements Update {
    final String key;
    final Object ref;
    
    MapUpdate(String key, Object ref) {
      this.key = key;
      this.ref = ref;
    }
    
    @Override
    public PMap doUpdate(Object parent, Object latest) {
      return ((PMap) parent).plus(key, latest);
    }
    
    @Override
    public Object getRef() {
      return ref;
    }
  }
  
  private Object unwind(Stack<Update> updates, Object latest) {
    //Object parent = initParent;
    Object parent = null;
    while (!updates.empty()) {
      Update update = updates.pop();
      parent = update.getRef();
      parent = update.doUpdate(parent, latest);
      latest = parent;
    }
    return latest;
  }
  
  private final Map<Character, Symbol> symbols = new HashMap<>();
  
  /**
   * Constructs a new ContextTraversal.  Initializes the list of command symbols.
   */
  public ContextTraversal() {
    for (Symbol s : Symbol.values()) {
      symbols.put(s.repr, s);
    }
  }
  
  private List<Instruction> splitIntoInstructions(String command) {
    List<Instruction> insts = new ArrayList<>();
    char first = command.charAt(0);
    // Must start with instr
    if (!symbols.containsKey(first)) {
      throw new RuntimeException("Command must start with a symbol");
    }
    Symbol currSym = symbols.get(first);
    StringBuilder currSb = new StringBuilder();
    // Iterate over the rest
    for (int i = 1; i < command.length(); i++) {
      char c = command.charAt(i);
      if (symbols.containsKey(c)) {
        insts.add(new Instruction(currSym, currSb.toString()));
        currSym = symbols.get(c);
        currSb = new StringBuilder();
      } else {
        currSb.append(c);
      }
    }
    // Last one
    insts.add(new Instruction(currSym, currSb.toString()));
    return insts;
  }
  
  private Object getPtr(Object ptr, String payload) {
    if (ptr == null) {
      return null;
    }
    if (ptr instanceof PMap) {
      PMap pmap = (PMap) ptr;
      if (!(pmap.containsKey(payload))) {
        return null;
      } else {
        return pmap.get(payload);
      }
    } else if (ptr instanceof PSequence) {
      PSequence pseq = (PSequence) ptr;
      try {
        int idx = Integer.parseInt(payload);
        return pseq.get(idx);
      } catch (NumberFormatException nfe) {
        throw new ContextTraversalException(
            fmt("List index must be an int, but %s was given", payload));
      }
    } else {
      throw new ContextTraversalException(
          fmt("Cannot subreference an object of type %s", ptr.getClass()));
    }
  }
  
  /**
   * Retrieves the object located at the specified Context DSL path or null if nothing found
   * @param map The map to retrieve from
   * @param path Context DSL path
   * @return Object at path or null
   */
  public Object get(PMap<String, Object> map, String path) {
    List<Instruction> insts = splitIntoInstructions(path);
    Object ptr = map;
    for (Instruction inst : insts) {
      String payload = inst.payload;
      switch (inst.symbol) {
        case SLASH:
          ptr = (PMap) getPtr(ptr, payload);
          break;
        case PIPE:
          ptr = (PSequence) getPtr(ptr, payload);
          break;
        case DOT:
          ptr = (String) getPtr(ptr, payload);
          break;
        case HASH:
          ptr = (Number) getPtr(ptr, payload);
          break;
        case QM:
          ptr = (Boolean) getPtr(ptr, payload);
          break;
        default:
          throw new ContextTraversalException("Unknown Instruction type: " + inst.symbol);
      }
    }
    return ptr;
  }
  
  /**
   * Removes the object located at the specified Context DSL path from the Context
   * @param map Map where the object lives
   * @param path Context DSL path
   * @return Context with the object removed
   */
  public Context remove(PMap<String, Object> map, String path) {
    List<Instruction> insts = splitIntoInstructions(path);
    Object ptr = map;
    int instIdx = 0;
    Stack<Update> updateStack = new Stack<>();
    while (instIdx < insts.size()) {
      Instruction inst = insts.get(instIdx++);
      boolean isLastInstruction = instIdx == insts.size();
      String payload = inst.payload;
      switch (inst.symbol) {
        case SLASH:
        case PIPE:
          if (ptr instanceof PMap) {
            PMap pmap = (PMap) ptr;
            if (isLastInstruction) {
              pmap = pmap.minus(payload);
              ptr = pmap;
            } else {
              updateStack.push(new MapUpdate(payload, pmap));
              ptr = pmap.get(payload);
            }
          } else if (ptr instanceof PSequence) {
            int payloadIdx = Integer.parseInt(payload);
            PSequence plist = (PSequence) ptr;
            if (isLastInstruction) {
              plist = plist.minus(payloadIdx);
              ptr = plist;
            } else {
              updateStack.push(new ListUpdate(payloadIdx, plist));
              ptr = plist.get(payloadIdx);
            }
          } else {
            throw new ContextTraversalException("Illegal expression: " + path);
          }
          break;
        case DOT:
        case HASH:
        case QM:
          if (ptr instanceof PMap) {
            PMap pmap = (PMap) ptr;
            pmap = pmap.minus(payload);
            ptr = pmap;
          } else if (ptr instanceof PSequence) {
            PSequence plist = (PSequence) ptr;
            int payloadIdx = Integer.parseInt(payload);
            plist = plist.minus(payloadIdx);
            ptr = plist;
          } else {
            throw new ContextTraversalException("Illegal expression: " + path);
          }
          break;
        default:
          throw new ContextTraversalException("Illegal expression: " + path);
      }
    }
    PMap newMap = (PMap) unwind(updateStack, ptr);
    return new Context(newMap);
  }
  
  /**
   * Puts the specified value at the Context DSL path in the Context
   * @param map Map to put changes to
   * @param path Context DSL path to where value will be put
   * @param value Value to assign
   * @return Context with the additional content added
   */
  public Context put(PMap<String, Object> map, String path, Object value) {
    List<Instruction> insts = splitIntoInstructions(path);
    Object ptr = map;
    int instIdx = 0;
    Stack<Update> updateStack = new Stack<>();
    while (instIdx < insts.size()) {
      Instruction inst = insts.get(instIdx++);
      boolean isLastInstruction = instIdx == insts.size();
      String payload = inst.payload;
      switch (inst.symbol) {
        case SLASH:
          if (ptr instanceof PMap) { /* Put map on map */
            PMap pmap = (PMap) ptr;
            updateStack.push(new MapUpdate(payload, pmap));
            if (!pmap.containsKey(payload)) {
              Object newValue = EMPTY_MAP;
              if (value != null && instIdx == insts.size()) {
                newValue = value;
              }
              pmap = pmap.plus(payload, newValue);
            } else {
              if (value != null && instIdx == insts.size()) {
                pmap = pmap.plus(payload, value);
              }
            }
            ptr = pmap.get(payload);
          } else if (ptr instanceof PSequence) {
            ptr = putCollectionOnList((PSequence) ptr, payload, value, updateStack,
                isLastInstruction);
          }
          break;
        case PIPE:
          if (ptr instanceof PMap) { /* Put list on map */
            PMap pmap = (PMap) ptr;
            if (!pmap.containsKey(payload)) {
              PSequence newList;
              updateStack.push(new MapUpdate(payload, pmap));
              if (value instanceof PSequence) {
                newList = (PSequence) value;
              } else {
                newList = (PSequence) EMPTY_LIST;
                if (value != null && instIdx == insts.size()) {
                  newList = newList.plus(value);
                }
              }
              pmap = pmap.plus(payload, newList);
              ptr = pmap.get(payload);
            } else {
              PSequence newList = (PSequence) pmap.get(payload);
              updateStack.push(new MapUpdate(payload, pmap));
              if (value != null && instIdx == insts.size()) {
                newList = newList.plus(value);
                pmap = pmap.plus(payload, newList);
              }
              ptr = pmap.get(payload);
            }
          } else if (ptr instanceof PSequence) {
            ptr = putCollectionOnList((PSequence) ptr, payload, value, updateStack,
                isLastInstruction);
          }
          break;
        case DOT:
        case HASH:
        case QM:
          if (inst.symbol.equals(Symbol.QM)) {
            value = Boolean.parseBoolean(value.toString());
          }
          if (ptr instanceof PMap) {
            PMap pmap = (PMap) ptr;
            updateStack.push(new MapUpdate(payload, pmap));
            pmap = pmap.plus(payload, value);
            ptr = pmap.get(payload);
          } else if (ptr instanceof PSequence) {
            int payloadIdx = Integer.parseInt(payload);
            PSequence plist = (PSequence) ptr;
            updateStack.push(new ListUpdate(payloadIdx, plist));
            if (plist.size() > payloadIdx) {
              plist = plist.minus(payloadIdx);
            }
            plist = plist.plus(value);
            ptr = plist.get(payloadIdx);
          }
          break;
        default:
          throw new RuntimeException();
      }
    }
    
    PMap newMap = (PMap) unwind(updateStack, ptr);
    return new Context(newMap);
  }
  
  private Object putCollectionOnList(PSequence list, String key, Object value,
                                     Stack<Update> updateStack, boolean isLastInstruction) {
    int keyIdx = Integer.parseInt(key);
    updateStack.push(new ListUpdate(keyIdx, list));
    if (list.size() > keyIdx) {
      list = list.minus(keyIdx);
    }
    Object newValue = value != null && isLastInstruction ? value : EMPTY_MAP;
    list = list.plus(keyIdx, newValue);
    return list.get(keyIdx);
  }
}

