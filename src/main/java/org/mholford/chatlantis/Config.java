package org.mholford.chatlantis;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.mholford.chatlantis.bot.BotConfig;

import java.util.List;

/**
 * Encapsulates the base level of configuration for Chatlantis.  It is
 * composed only of multiple Bot configurations.  This class is typically
 * instantiated by deserialization of the chatlantis.json config file
 * during startup of Chatlantis.
 */
public class Config {
  @JsonProperty("bots")
  private List<BotConfig> botConfigs;
  
  /**
   * Gets all configured bots
   * @return Bots
   */
  public List<BotConfig> getBotConfigs() {
    return botConfigs;
  }
  
  /**
   * Set the list of configured Bots to the specified value
   * @param botConfigs list of Bots
   */
  public void setBotConfigs(List<BotConfig> botConfigs) {
    this.botConfigs = botConfigs;
  }
}
