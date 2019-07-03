package org.mholford.chatlantis.lookup;

import org.junit.Before;
import org.junit.Test;
import org.mholford.chatlantis.*;
import org.mholford.chatlantis.utterance.Token;
import org.mholford.chatlantis.utterance.TokenFactory;
import org.mholford.chatlantis.utterance.Utterance;
import org.mholford.chatlantis.utterance.UtteranceFactory;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class FSTLookupTest implements Utils {
  FSTLookupTable fst;
  UtteranceFactory uf;
  Conversation conv;
  TokenFactory tf = TokenFactory.get();
  
  @Before
  public void before() {
    fst = new FSTLookupTable();
    uf = UtteranceFactory.get();
    ConversationFactory cf = ConversationFactory.get();
    User user = UserFactory.get().createNewUser("Anon");
    conv = cf.createNew(user);
  }
  
  private void initFst(String dict) throws IOException {
    fst.init(stringMapOf(FSTLookupTable.DICT_PARAM, dict));
  }
  
  @Test
  public void testSimple() throws IOException {
    initFst("simple-LUT.csv");
    Utterance u = uf.createNew("i want to open a ticket", conv);
    
    String o = fst.lookup(u);
    assertEquals("$utt:/intent.name -> createTicket", o);
  }
  
  @Test
  public void testWC() throws IOException {
    initFst("wc-LUT.csv");
    Utterance u = uf.createNew("open a blocker ticket", conv);
    String o = fst.lookup(u);
    assertEquals("$utt:/intent.name -> createTicket; " +
        "$utt:/objects/ticket.priority -> blocker", o);
  }
  
  @Test
  public void testWCMultiword() throws IOException {
    initFst("wc-LUT.csv");
    Utterance u = uf.createNew("open a super blocker ticket", conv);
    String o = fst.lookup(u);
    assertEquals("$utt:/intent.name -> createTicket; " +
        "$utt:/objects/ticket.priority -> super blocker", o);
  }
  
  @Test
  public void testWCAtEnd() throws IOException {
    initFst("wc-LUT.csv");
    Utterance u = uf.createNew("open a ticket called fix the bugs", conv);
    String o = fst.lookup(u);
    assertEquals("$utt:/intent.name -> createTicket; " +
        "$utt:/objects/ticket.title -> fix the bugs", o);
  }
  
  @Test
  public void testWCAtBeginning() throws IOException {
    initFst("wc-LUT.csv");
    Utterance u = uf.createNew("george washington gets a ticket", conv);
    String o = fst.lookup(u);
    assertEquals("$utt:/intent.name -> createTicket; " +
        "$utt:/objects/ticket.assignee -> george washington", o);
  }
  
  @Test
  public void testWCMultiple() throws IOException {
    initFst("wc-LUT.csv");
    Utterance u = uf.createNew("open a blocker ticket called fix all bugs", conv);
    String o = fst.lookup(u);
    assertEquals("$utt:/intent.name -> createTicket; " +
        "$utt:/objects/ticket.priority -> blocker; " +
        "$utt:/objects/ticket.title -> fix all bugs", o);
  }
  
  @Test
  public void testBadMatch() throws IOException {
    initFst("wc-LUT.csv");
    Utterance u = uf.createNew("open a ticket", conv);
    assertNull(fst.lookup(u));
  }
  
  @Test
  public void testShortMatch() throws IOException {
    initFst("tix-gen-LUT.csv");
    Utterance u = uf.createNew("open a ticket", conv);
    assertEquals("$utt:/intent.name -> createTicket", fst.lookup(u));
  }
  
  @Test
  public void testEnt() throws IOException {
    initFst("ent-LUT.csv");
    List<Token> tokens = listOf(
        tf.createLiteral("open"), tf.createLiteral("a"), tf.createEntity("PRIO", "blocker"),
        tf.createLiteral("ticket"));
    Utterance u = uf.create(tokens, conv);
    String o = fst.lookup(u);
    assertEquals("$utt:/intent.name -> createTicket; " +
        "$utt:/objects/ticket.priority -> blocker", o);
  }
  
  @Test
  public void testEntMultiword() throws IOException {
    initFst("ent-LUT.csv");
    List<Token> tokens = listOf(
        tf.createLiteral("open"), tf.createLiteral("a"), tf.createEntity("PRIO", "super blocker"),
        tf.createLiteral("ticket"));
    Utterance u = uf.create(tokens, conv);
    String o = fst.lookup(u);
    assertEquals("$utt:/intent.name -> createTicket; " +
        "$utt:/objects/ticket.priority -> super blocker", o);
  }
  
  @Test
  public void testEntAtEnd() throws IOException {
    initFst("ent-LUT.csv");
    List<Token> tokens = listOf(
        tf.createLiteral("open"), tf.createLiteral("a"), tf.createLiteral("ticket"),
        tf.createLiteral("for"), tf.createEntity("EMP", "matt holford"));
    Utterance u = uf.create(tokens, conv);
    String o = fst.lookup(u);
    assertEquals("$utt:/intent.name -> createTicket; " +
        "$utt:/objects/ticket.assignee -> matt holford", o);
  }
  
  @Test
  public void testEntAtBeginning() throws IOException {
    initFst("ent-LUT.csv");
    List<Token> tokens = listOf(
        tf.createEntity("EMP", "george washington"), tf.createLiteral("gets"),
        tf.createLiteral("a"), tf.createLiteral("ticket"));
    Utterance u = uf.create(tokens, conv);
    String o = fst.lookup(u);
    assertEquals("$utt:/intent.name -> createTicket; " +
        "$utt:/objects/ticket.assignee -> george washington", o);
  }
  
  @Test
  public void testEntMultiple() throws IOException {
    initFst("ent-LUT.csv");
    List<Token> tokens = listOf(
        tf.createEntity("EMP", "george washington"), tf.createLiteral("gets"),
        tf.createLiteral("a"), tf.createEntity("PRIO", "super blocker"),
        tf.createLiteral("ticket"));
    Utterance u = uf.create(tokens, conv);
    String o = fst.lookup(u);
    assertEquals("$utt:/intent.name -> createTicket; " +
        "$utt:/objects/ticket.assignee -> george washington; " +
        "$utt:/objects/ticket.priority -> super blocker", o);
  }
  
  @Test
  public void testEntAndWC() throws IOException {
    initFst("ent-LUT.csv");
    List<Token> tokens = listOf(
        tf.createLiteral("open"), tf.createLiteral("a"), tf.createEntity("PRIO", "super blocker"),
        tf.createLiteral("ticket"), tf.createLiteral("called"), tf.createLiteral("fix"),
        tf.createLiteral("bugs"));
    Utterance u = uf.create(tokens, conv);
    String o = fst.lookup(u);
    assertEquals("$utt:/intent.name -> createTicket; " +
        "$utt:/objects/ticket.priority -> super blocker; " +
        "$utt:/objects/ticket.title -> fix bugs", o);
  }
  
  @Test
  public void testWC2() throws IOException {
    initFst("wc-LUT2.csv");
    Utterance u = uf.createNew("open a ticket for someone", conv);
    String o = fst.lookup(u);
    assertEquals("match 1", o);
  }
}
