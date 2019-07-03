package org.mholford.chatlantis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mholford.chatlantis.bot.Bot;
import org.mholford.chatlantis.bot.BotConfig;
import org.mholford.chatlantis.bot.BotRegistry;
import org.mholford.chatlantis.utterance.Utterance;
import org.mholford.chatlantis.utterance.UtteranceFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the global Chatlantis object.  It is a singleton.  On initialization, it
 * will attempt to read the chatlantis.json configuration file and populate all the helper
 * classes associated with Chatlantis utterance processing.  The Chatlantis instance retains
 * a map of conversations and of users.  The speak() method provides the main entry point to
 * Utterance Processing.  The raw user input is sent to the appropriate bot which provides
 * a spoken answer for Chatlantis.
 * </p>
 */
public class Chatlantis implements Utils {
  private static Chatlantis INSTANCE;
  private Map<String, Conversation> conversations;
  private Map<String, User> users;
  private final ObjectMapper objectMapper = new ObjectMapper();
  
  private Chatlantis() throws IOException, ReflectiveOperationException {
    users = new HashMap<>();
    conversations = new HashMap<>();
    initConfig();
  }
  
  /**
   * Gets the singleton instance, creating it if necessary.  Creating the singleton
   * instance will trigger initialization from the chatlantis.json configuration file
   * @return Singleton instance, fully configured
   * @throws IOException If could not read configuration
   * @throws ReflectiveOperationException If could not instantiate classes
   */
  public static Chatlantis get() throws IOException, ReflectiveOperationException {
    if (INSTANCE == null) {
      INSTANCE = new Chatlantis();
    }
    return INSTANCE;
  }
  
  /**
   * Deletes the singleton instance.  This is usually so that it can reconfigured and is
   * primarily useful for testing purposes.
   */
  public static void clear() {
    INSTANCE = null;
  }
  
  /**
   * Main entry point to Chatlantis utterance processing.  Looks up provided user, conversation
   * and bot.  Creates a new Utterance based on this and the raw user input.  This initial
   * Utterance gets passed to the specified Bot who in turn assigns it to a Workflow.  The
   * response from the Bot is wrapped in a ChatlantisAnswer object.  This holds the spoken
   * response and reference to the Conversation, so that it can be used in the next Utterance.
   * @param input Raw user input
   * @param user User id
   * @param conv Conversation id
   * @param botname Name of Bot to use
   * @return Chatlantis Answer (spoken response + reference to Conversation)
   * @throws IOException If something went wrong
   */
  public ChatlantisAnswer speak(String input, String user, String conv, String botname)
      throws IOException {
    if (!users.containsKey(user)) {
      User newUser = UserFactory.get().createNewUser(user);
      users.put(user, newUser);
    }
    User currUser = users.get(user);
    
    if (conv == null) {
      Conversation newConv = ConversationFactory.get().createNew(currUser);
      conv = newConv.getId();
      conversations.put(conv, newConv);
    }
    Conversation currConv = conversations.get(conv);
    Bot bot = BotRegistry.get().find(botname);
    Utterance utt = UtteranceFactory.get().createNew(input, currConv);
    String answer = bot.answer(utt);
    return new ChatlantisAnswer(answer, currConv);
  }
  
  private void initConfig() throws IOException, ReflectiveOperationException {
    String overrideConfigPath = System.getProperty("chatlantis.config");
    Config config;
    if (overrideConfigPath != null) {
      File configFile = new File(overrideConfigPath);
      config = objectMapper.readValue(configFile, Config.class);
    } else {
      InputStream defConfig = getResource("chatlantis.json");
      config = objectMapper.readValue(defConfig, Config.class);
    }
  
    List<Bot> bots = new ArrayList<>();
    for (BotConfig bc : config.getBotConfigs()) {
      bots.add(bc.init());
    }
    BotRegistry.get().init(bots);
  }
}
