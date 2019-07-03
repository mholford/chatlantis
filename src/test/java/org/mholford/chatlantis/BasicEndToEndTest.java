package org.mholford.chatlantis;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class BasicEndToEndTest implements Utils {
  
  @Test
  public void test() throws IOException, ReflectiveOperationException {
    String botname = "default";
    Chatlantis.clear();
    Chatlantis server = Chatlantis.get();
    
    String user = "Matt";
    String input = "I want to open a ticket";
    String response = server.speak(input, user, null, botname).getAnswer();
    
    List<String> possibleResponses = listOf("What should we call this ticket?",
        "Who should we assign this ticket to?", "What priority should we set?",
        "How long should we estimate?");
    assertThat(response).isIn(possibleResponses);
  }
}
