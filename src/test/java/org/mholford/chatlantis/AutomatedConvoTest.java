package org.mholford.chatlantis;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.fail;
import static org.junit.Assert.assertEquals;

public class AutomatedConvoTest implements Utils {
  private Chatlantis server;
  private String botname;
  private String user;
  private int THRESHOLD = 100;
  
  @Before
  public void before() throws IOException, ReflectiveOperationException {
    botname = "default";
    user = "Matt";
    Chatlantis.clear();
    server = Chatlantis.get();
  }
  
  @Test
  public void test1() throws IOException {
    String goal = "Created ticket ABC-001 in JIRA";
    Map<String, String> map = stringMapOf(
        "What should we call this ticket?", "hello",
        "Who should we assign this ticket to?", "matt",
        "What priority should we set?", "high",
        "How long should we estimate?", "10 hours",
        "You are creating a ticket called hello, assigned to matt, " +
            "with priority of high, and an estimate of 10 hours.\nIs that okay?", "yes");
    String input = "i want to open a ticket";
    ChatlantisAnswer answer = loopUntilGoal(input, goal, map);
    assertEquals(goal, answer.getAnswer());
  }
  
  @Test
  public void test2() throws IOException {
    String goal = "Created ticket ABC-001 in JIRA";
    Map<String, String> map = stringMapOf(
        "How long should we estimate?", "10 hours",
        "You are creating a ticket called hello, assigned to matt, " +
            "with priority of blocker, and an estimate of 10 hours.\nIs that okay?", "yes");
    String input = "lets assign matt a blocker ticket called hello";
    ChatlantisAnswer answer = loopUntilGoal(input, goal, map);
    assertEquals(goal, answer.getAnswer());
  }
  
  @Test
  public void test3() throws IOException {
    String goal = "Created ticket ABC-001 in JIRA";
    Map<String, String> map = stringMapOf(
        "How long should we estimate?", "10 hours",
        "You are creating a ticket called hello, assigned to matt, " +
            "with priority of blocker, and an estimate of 10 hours.\nIs that okay?", "no",
        "What would you like to change?", "assign to steve instead",
        "You changed assignee to steve.\n\nYou are creating a ticket called hello, assigned to " +
            "steve, with priority of blocker, and an estimate of 10 hours.\nIs that okay?", "yes");
    String input = "lets assign matt a blocker ticket called hello";
    ChatlantisAnswer answer = loopUntilGoal(input, goal, map);
    assertEquals(goal, answer.getAnswer());
  }
  
  private ChatlantisAnswer loopUntilGoal(String input, String goal, Map<String, String> map)
      throws IOException {
    String convId = null;
    ChatlantisAnswer answer = server.speak(input, user, convId, botname);
    int tries = 0;
    while (!answer.getAnswer().equals(goal) && tries++ < THRESHOLD) {
      String a = answer.getAnswer();
      input = map.get(a);
      if (input == null) {
        throw new IOException("No response to match input: " + a);
      }
      convId = answer.getConversation().getId();
      answer = server.speak(input, user, convId, botname);
    }
    if (tries >= THRESHOLD) {
      throw new RuntimeException("Unbroken loop; goal condition not reached");
    }
    return answer;
  }
}
