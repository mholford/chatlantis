package org.mholford.chatlantis.utterance;

import org.junit.Test;
import org.mholford.chatlantis.Utils;

import java.util.List;
import static org.junit.Assert.assertEquals;

public class UtteranceTest implements Utils {
  
  @Test
  public void testFindLiteralTokenSpans() {
    UtteranceFactory uf = UtteranceFactory.get();
    TokenFactory tf = TokenFactory.get();
    Utterance u = uf.create(listOf(tf.createEntity("X", "X"), tf.createLiteral("hello"),
        tf.createLiteral("there"), tf.createEntity("Y", "Y"), tf.createLiteral("how"),
        tf.createLiteral("are"), tf.createLiteral("you"), tf.createEntity("Z", "Z")), null);
    List<LiteralTokenSpan> lts = u.findLiteralTokenSpans();
    assertEquals(2, lts.size());
    LiteralTokenSpan lts1 = lts.get(0);
    assertEquals(1, lts1.getStart());
    assertEquals(2, lts1.getTokens().size());
    assertEquals("hello", lts1.getTokens().get(0).getValue());
    assertEquals("there", lts1.getTokens().get(1).getValue());
    LiteralTokenSpan lts2 = lts.get(1);
    assertEquals(4, lts2.getStart());
    assertEquals(3, lts2.getTokens().size());
    assertEquals("how", lts2.getTokens().get(0).getValue());
    assertEquals("are", lts2.getTokens().get(1).getValue());
    assertEquals("you", lts2.getTokens().get(2).getValue());
    
    u = uf.create(listOf(tf.createLiteral("hello"), tf.createLiteral("there"),
        tf.createEntity("Y", "Y"), tf.createLiteral("how"),
        tf.createLiteral("are"), tf.createLiteral("you")), null);
    lts = u.findLiteralTokenSpans();
    assertEquals(2, lts.size());
    lts1 = lts.get(0);
    assertEquals(0, lts1.getStart());
    assertEquals(2, lts1.getTokens().size());
    assertEquals("hello", lts1.getTokens().get(0).getValue());
    assertEquals("there", lts1.getTokens().get(1).getValue());
    lts2 = lts.get(1);
    assertEquals(3, lts2.getStart());
    assertEquals(3, lts2.getTokens().size());
    assertEquals("how", lts2.getTokens().get(0).getValue());
    assertEquals("are", lts2.getTokens().get(1).getValue());
    assertEquals("you", lts2.getTokens().get(2).getValue());
  }
}
