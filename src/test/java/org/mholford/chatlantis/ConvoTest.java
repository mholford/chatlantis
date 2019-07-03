package org.mholford.chatlantis;

import java.io.IOException;
import java.util.Scanner;

public class ConvoTest implements Utils {
  
  private Chatlantis server;
  private String botname;
  private String user;
  
  public void init() throws IOException, ReflectiveOperationException {
    server = Chatlantis.get();
    user = "Matt";
    botname = "default";
    loop();
  }
  
  public void loop() throws IOException {
    Scanner console = new Scanner(System.in);
    System.out.println("hello");
    String convId = null;
    while(true) {
      String input = console.nextLine();
      ChatlantisAnswer answer = server.speak(input, user, convId, botname);
      System.out.println(answer.getAnswer());
      convId = answer.getConversation().getId();
      printContext(answer.getConversation().getContext());
    }
  }
  
  public static void main(String[] args) {
    try {
      new ConvoTest().init();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ReflectiveOperationException e) {
      e.printStackTrace();
    }
  }
}
