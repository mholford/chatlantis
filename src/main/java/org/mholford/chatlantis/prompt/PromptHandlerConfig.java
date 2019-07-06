package org.mholford.chatlantis.prompt;

import java.util.Map;

/**
 * Encapsulates user configuration of a Prompt handler.  Configuration
 * consists of: <ul>
 *   <li>Fully qualified class name of the PromptHandler implementation</li>
 *   <li>String->String map of properties to be applied to the PromptHandler</li>
 * </ul>
 * The PromptHandler is initialized (instantiating the specified class) behind the
 * scenes during startup of Chatlantis from chatlantis.json config file.
 */
public class PromptHandlerConfig {
  private String cls;
  private Map<String, String> props;
  
  /**
   * Gets the fully qualified class name of the Prompt Handler
   * @return Class name
   */
  public String getCls() {
    return cls;
  }
  
  /**
   * Sets the fully qualified class name of the Prompt handler to the specified value
   * @param cls Class name
   */
  public void setCls(String cls) {
    this.cls = cls;
  }
  
  /**
   * Gets the property map to be applied to the instantiated Prompt Handler
   * @return Property map
   */
  public Map<String, String> getProps() {
    return props;
  }
  
  /**
   * Set the property map to the specified value
   * @param props Property map
   */
  public void setProps(Map<String, String> props) {
    this.props = props;
  }
  
  /**
   * Initializes the prompt handler by instantiating the class and initializing it with
   * the property map.  This is called internally during startup of Chatlantis from configs
   * @return Initialized PromptHandler
   * @throws ReflectiveOperationException If something went wrong while instantiating the
   * PromptHandler
   */
  public PromptHandler init() throws ReflectiveOperationException {
    Class<PromptHandler> phc = (Class<PromptHandler>) Class.forName(cls);
    PromptHandler ph = phc.newInstance();
    ph.init(props);
    return ph;
  }
  
  /**
   * Gets the default PromptHandler, which is DefaultPromptHandler
   * @return DefaultPromptHandler
   */
  public static PromptHandler getDefault() {
    return new DefaultPromptHandler();
  }
}
