package org.mholford.chatlantis.bot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Chatlantis maintains a simple registry of configured Bots.  It is backed by
 * a HashMap and gets populated when Chatlantis is initialized from a chatlantis.json
 * config file.  BotRegistry is a singleton class; the instance should be obtained by
 * calling BotRegistry.get().
 */
public class BotRegistry {
  private final Map<String, Bot> map;
  private static BotRegistry INSTANCE;
  
  private BotRegistry() {
    map = new HashMap<>();
  }
  
  /**
   * Gets the singleton instance, creating it if necessary.
   * @return Singleton instance
   */
  public static BotRegistry get() {
    if (INSTANCE == null) {
      INSTANCE = new BotRegistry();
    }
    return INSTANCE;
  }
  
  /**
   * Initializes the BotRegistry from a list of configured Bots.  It creates the
   * backing map by keying each Bot against its name.
   * @param bots List of configured bots
   */
  public void init(List<Bot> bots) {
    bots.forEach(b -> map.put(b.getName(), b));
  }
  
  /**
   * Returns the bot with the specified name
   * @param name Name of bot
   * @return Bot
   */
  public Bot find(String name) {
    return map.get(name);
  }
  
  /**
   * Returns the size of the backing map
   * @return number of registered bots
   */
  public int size() {
    return map.size();
  }
}
