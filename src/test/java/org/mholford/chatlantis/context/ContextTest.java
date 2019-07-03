package org.mholford.chatlantis.context;

import org.junit.Test;
import org.pcollections.HashTreePMap;
import org.pcollections.PMap;
import org.pcollections.PSequence;

import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

public class ContextTest {
  private final static ContextFactory cf = ContextFactory.get();
  
  @Test
  public void testContextPut() {
    Context testContext = cf.newContext();
    Context ctx2 = testContext._put("hello", "there");
    assertEquals(0, testContext.size());
    assertEquals(1, ctx2.size());
    assertEquals("there", ctx2._get("hello"));
    
    Context ctx3 = ctx2._put("goodbye", "there");
    assertEquals(2, ctx3.size());
    assertEquals("there", ctx3._get("goodbye"));
  }
  
  @Test
  public void testMapOneLevel() {
    String expr = "/objects";
    Context c = cf.newContext().put(expr, null);
    assertEquals(1, c.size());
    assertTrue(c.containsKey("objects"));
    
    PMap<String, Object> map = (PMap<String, Object>) c.get(expr);
    assertEquals(0, map.size());
  }
  
  @Test
  public void testPutMapTwoLevel() {
    String expr = "/objects/tickets";
    Context c = cf.newContext().put(expr, null);
    assertEquals(1, c.size());
    assertTrue(c.containsKey("objects"));
    PMap<String, Object> l2 = (PMap<String, Object>) c._get("objects");
    assertEquals(1, l2.size());
    assertTrue(l2.containsKey("tickets"));
    
    PMap<String, Object> map = (PMap<String, Object>) c.get(expr);
    assertEquals(0, map.size());
  }
  
  @Test
  public void testPutMapThreeLevel() {
    String expr = "/objects/ticket/properties";
    Context c = cf.newContext().put(expr, null);
    assertEquals(1, c.size());
    assertTrue(c.containsKey("objects"));
    PMap<String, Object> l2 = (PMap<String, Object>) c._get("objects");
    assertEquals(1, l2.size());
    assertTrue(l2.containsKey("ticket"));
    PMap<String, Object> l3 = (PMap<String, Object>) l2.get("ticket");
    assertEquals(1, l3.size());
    assertTrue(l3.containsKey("properties"));
    Context ca = c.put("/objects/car", null);
    assertEquals(1, ca.size());
    assertTrue(ca.containsKey("objects"));
    l2 = (PMap<String, Object>) ca._get("objects");
    assertEquals(2, l2.size());
    assertTrue(l2.containsKey("ticket"));
    assertTrue(l2.containsKey("car"));
    l3 = (PMap<String, Object>) l2.get("ticket");
    assertEquals(1, l3.size());
    assertTrue(l3.containsKey("properties"));
    
    PMap<String, Object> map = (PMap<String, Object>) c.get(expr);
    assertEquals(0, map.size());
  }
  
  @Test
  public void testList() {
    String expr = "|a";
    Context c = cf.newContext().put(expr, null);
    assertEquals(1, c.size());
    assertTrue(c.containsKey("a"));
    assertTrue(c._get("a") instanceof PSequence);
    
    PSequence list = (PSequence) c.get(expr);
    assertEquals(0, list.size());
  }
  
  @Test
  public void testPutMapNestedList() {
    String expr = "/a/b|c";
    Context c = cf.newContext().put(expr, "hey!");
    assertEquals(1, c.size());
    assertTrue(c.containsKey("a"));
    PMap<String, Object> l2 = (PMap<String, Object>) c._get("a");
    assertEquals(1, l2.size());
    assertTrue(l2.containsKey("b"));
    PMap<String, Object> l3 = (PMap<String, Object>) l2.get("b");
    assertEquals(1, l3.size());
    assertTrue(l3.containsKey("c"));
    assertTrue(l3.get("c") instanceof List);
    assertTrue(((List) l3.get("c")).contains("hey!"));
    
    PSequence list = (PSequence) c.get(expr);
    assertEquals(1, list.size());
    assertTrue(list.contains("hey!"));
  }
  
  @Test
  public void testPutMapListOfMaps() {
    String expr = "/a|b/0";
    Context c = cf.newContext().put(expr, null);
    assertEquals(1, c.size());
    assertTrue(c.containsKey("a"));
    PMap<String, Object> l2 = (PMap<String, Object>) c._get("a");
    assertEquals(1, l2.size());
    assertTrue(l2.containsKey("b"));
    List l = (List) l2.get("b");
    assertEquals(1, l.size());
    assertTrue(l.get(0) instanceof PMap);
    
    PMap<String, Object> map = (PMap<String, Object>) c.get(expr);
    assertEquals(0, map.size());
  }
  
  @Test
  public void testDot() {
    String expr = ".a";
    Context c = cf.newContext().put(expr, "hello");
    assertEquals(1, c.size());
    assertTrue(c.containsKey("a"));
    assertEquals("hello", c._get("a"));
    
    assertEquals("hello", c.get(expr));
  }
  
  @Test
  public void testDotInMap() {
    String expr = "/a.x";
    Context c = cf.newContext().put(expr, "hello");
    assertEquals(1, c.size());
    assertTrue(c.containsKey("a"));
    PMap<String, Object> l2 = (PMap<String, Object>) c._get("a");
    assertEquals(1, l2.size());
    assertTrue(l2.containsKey("x"));
    assertEquals("hello", l2.get("x"));
    
    assertEquals("hello", c.get(expr));
  }
  
  @Test
  public void testDotInList() {
    String expr = "|a.0";
    Context c = cf.newContext().put(expr, "hello");
    assertEquals(1, c.size());
    assertTrue(c.containsKey("a"));
    PSequence l2 = (PSequence) c._get("a");
    assertEquals(1, l2.size());
    assertEquals("hello", l2.get(0));
    
    assertEquals("hello", c.get(expr));
  }
  
  @Test
  public void testHash() {
    String expr = "#a";
    Context c = cf.newContext().put(expr, 34);
    assertEquals(1, c.size());
    assertEquals(34, c._get("a"));
    
    assertEquals(34, c.get(expr));
  }
  
  @Test
  public void testHashInMap() {
    String expr = "/a#x";
    Context c = cf.newContext().put(expr, 342);
    assertEquals(1, c.size());
    PMap<String, Object> l2 = (PMap<String, Object>) c._get("a");
    assertEquals(1, l2.size());
    assertEquals(342, l2.get("x"));
    
    assertEquals(342, c.get(expr));
  }
  
  @Test
  public void testHashInList() {
    String expr = "|a#0";
    Context c = cf.newContext().put(expr, 253);
    assertEquals(1, c.size());
    PSequence l2 = (PSequence) c._get("a");
    assertEquals(1, l2.size());
    assertEquals(253, l2.get(0));
    
    assertEquals(253, c.get(expr));
  }
  
  @Test
  public void testQM() {
    String expr = "?a";
    Context c = cf.newContext().put(expr, false);
    assertEquals(1, c.size());
    assertEquals(false, c._get("a"));
    
    assertFalse((Boolean) c.get(expr));
  }
  
  @Test
  public void testQMInMap() {
    String expr = "/a?x";
    Context c = cf.newContext().put(expr, false);
    assertEquals(1, c.size());
    PMap<String, Object> l2 = (PMap<String, Object>) c._get("a");
    assertEquals(1, l2.size());
    assertEquals(false, l2.get("x"));
    
    assertFalse((Boolean) c.get(expr));
  }
  
  @Test
  public void testQMInList() {
    String expr = "|a?0";
    Context c = cf.newContext().put(expr, false);
    assertEquals(1, c.size());
    PSequence l2 = (PSequence) c._get("a");
    assertEquals(1, l2.size());
    assertEquals(false, l2.get(0));
    
    assertEquals(false, c.get(expr));
  }
  
  @Test
  public void testInsertInMap() {
    Context c = cf.newContext().put("/a.x", "hello");
    c = c.put("/a.y", "goodbye");
    assertEquals(1, c.size());
    PMap<String, Object> l2 = (PMap<String, Object>) c._get("a");
    assertEquals(2, l2.size());
    assertEquals("hello", l2.get("x"));
    assertEquals("goodbye", l2.get("y"));
    
    assertEquals("hello", c.get("/a.x"));
    assertEquals("goodbye", c.get("/a.y"));
  }
  
  @Test
  public void testInsertInList() {
    Context c = cf.newContext().put("|a", "hello");
    c = c.put("|a", "goodbye");
    assertEquals(1, c.size());
    PSequence l2 = (PSequence) c._get("a");
    assertEquals(2, l2.size());
    assertTrue(l2.contains("hello"));
    assertTrue(l2.contains("goodbye"));
    
    PSequence seq = (PSequence) c.get("|a");
    assertEquals(2, seq.size());
    assertTrue(seq.contains("hello"));
    assertTrue(seq.contains("goodbye"));
  }
  
  @Test
  public void testInsertMap() {
    PMap<String, Object> map = HashTreePMap.empty();
    map = map.plus("k1", "v1");
    map = map.plus("k2", "v2");
    Context c = cf.newContext().put("/a/b", map);
    
    assertEquals("v1", c.get("/a/b.k1"));
    assertEquals("v2", c.get("/a/b.k2"));
  }
  
  @Test
  public void testRemove() {
    Context c = cf.newContext().put("/a/b.x", "start");
    c = c.put("/a/b.y", "middle");
    c = c.put("/a/b.z", "end");
    
    c = c.remove("/a/b.y");
    assertEquals("start", c.get("/a/b.x"));
    assertEquals("end", c.get("/a/b.z"));
    assertNull(c.get("/a/b.y"));
    
    c = c.remove("/a/b");
    Object amap = c.get("/a");
    assertEquals(0, ((PMap) amap).size());
  }
}
