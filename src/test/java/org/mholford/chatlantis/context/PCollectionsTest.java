package org.mholford.chatlantis.context;

import org.junit.Test;
import org.pcollections.*;

import java.util.Stack;

import static org.junit.Assert.assertEquals;

public class PCollectionsTest {
  
  private interface Update {
    Object doUpdate(Object parent, Object latest);
    Object getRef();
  }
  
  private class ListUpdate implements Update {
    final int idx;
    final PCollection ref;
    
    ListUpdate(int idx, PCollection ref) {
      this.idx = idx;
      this.ref = ref;
    }
    @Override
    public PCollection doUpdate(Object parent, Object latest) {
      PSequence output = (PSequence) parent;
      if (output.size() > idx) {
        output = output.minus(idx);
      }
      output = output.plus(latest);
      return output;
    }
  
    @Override
    public PCollection getRef() {
      return ref;
    }
  
    public int getIdx() {
      return idx;
    }
  }
  
  private class MapUpdate implements Update {
    final String key;
    final PMap ref;
    
    MapUpdate(String key, PMap ref) {
      this.key = key;
      this.ref = ref;
    }
    @Override
    public PMap doUpdate(Object parent, Object latest) {
      return ((PMap)parent).plus(key, latest);
    }
  
    @Override
    public PMap getRef() {
      return ref;
    }
  }
  
  private Object unwind(Stack<Update> updates, Object latest, Object initParent) {
    Object parent = initParent;
    while (!updates.empty()) {
      Update update = updates.pop();
      parent = update.getRef();
      parent = update.doUpdate(parent, latest);
      latest = parent;
    }
    return parent;
  }
  
  @Test
  public void test() {
    HashPMap<String, Object> map = HashTreePMap.empty();
    map = map.plus("a", HashTreePMap.empty());
    map = map.plus("a", "other");
    assertEquals("other", map.get("a"));
  
    PSequence<Object> list = TreePVector.empty();
    list = list.plus("abc");
    list = list.plus("abc");
    assertEquals(2, list.size());
  }
  
  @Test
  public void test2() {
    Stack<Update> updateStack = new Stack<>();
    HashPMap<String, Object> map = HashTreePMap.empty();
    updateStack.push(new MapUpdate("a", map));
    map = map.plus("a", HashTreePMap.empty());
    
    HashPMap<String, Object> l1 = (HashPMap<String, Object>) map.get("a");
    updateStack.push(new MapUpdate("b", l1));
    l1 = l1.plus("b", HashTreePMap.empty());
    
    HashPMap<String, Object> l2 = (HashPMap<String, Object>) l1.get("b");
    updateStack.push(new MapUpdate("c", l2));
    l2 = l2.plus("c", HashTreePMap.empty());
    
    HashPMap<String, Object> l3 = (HashPMap<String, Object>) l2.get("c");
    l3 = l3.plus("x", "y");
    
    Object unwound = unwind(updateStack, l3, l2);
    unwound.getClass();
    //l2 = l2.plus("c", l3);
    //l1 = l1.plus("b", l2);
    //map = map.plus("a", l1);
    //assertEquals(1, map.size());
  }
  
  @Test
  public void test3() {
    Stack<Update> updateStack = new Stack<>();
    HashPMap<String, Object> map = HashTreePMap.empty();
    updateStack.push(new MapUpdate("a", map));
    map = map.plus("a", HashTreePMap.empty());
    
    HashPMap<String, Object> l1 = (HashPMap<String, Object>) map.get("a");
    updateStack.push(new MapUpdate("b", l1));
    l1 = l1.plus("b", HashTreePMap.empty());
    
    HashPMap<String, Object> l2 = (HashPMap<String, Object>) l1.get("b");
    updateStack.push(new MapUpdate("c", l2));
    l2 = l2.plus("c", TreePVector.empty());
    
    PSequence l3 = (PSequence) l2.get("c");
    updateStack.push(new ListUpdate(0, l3));
    l3 = l3.plus(HashTreePMap.empty());
    
    HashPMap<String, Object> l4 = (HashPMap<String, Object>) l3.get(0);
    l4 = l4.plus("x", "y");
    Object unwound = unwind(updateStack, l4, l3);
    unwound.getClass();
  
    //l3 = l3.minus(0);
    //l3 = l3.plus(l4);
    //l2 = l2.plus("c", l3);
    //l1 = l1.plus("b", l2);
    //map = map.plus("a", l1);
    //assertEquals(1, map.size());
  }
}
