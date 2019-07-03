package org.mholford.chatlantis.action;

import java.util.Map;

/**
 * Encapsulates user configuration of an ActionProcessor.  Will usually be instantiated by
 * deserialization of chatlantis.json config.  Note that init() should be called
 * in order to reflectively create an instance of the ActionProcessor class.  This is done
 * automatically by BotConfig during initialization.
 */
public class ActionProcessorConfig {
  private String cls;
  private Map<String, String> props;
  
  /**
   * Gets the fully-qualified class name of the ActionProcessor to be created
   * @return Class name
   */
  public String getCls() {
    return cls;
  }
  
  /**
   * Sets the fully-qualified class name of the ActionProcessor to be created to the specified
   * value
   * @param cls Class name
   */
  public void setCls(String cls) {
    this.cls = cls;
  }
  
  /**
   * Gets the map of properties used to initialize the ActionProcessor
   * @return Map of properties
   */
  public Map<String, String> getProps() {
    return props;
  }
  
  /**
   * Set the map of properties used to initialize the ActionProcessor to the specified value
   * @param props Map of properties
   */
  public void setProps(Map<String, String> props) {
    this.props = props;
  }
  
  /**
   * Instantiate the ActionProcessor using reflection
   * @return ActionProcessor instance
   * @throws ReflectiveOperationException If could not create a new instance
   */
  public ActionProcessor init() throws ReflectiveOperationException {
    Class<ActionProcessor> apc = (Class<ActionProcessor>) Class.forName(cls);
    ActionProcessor ap = apc.newInstance();
    ap.init(props);
    return ap;
  }
}
