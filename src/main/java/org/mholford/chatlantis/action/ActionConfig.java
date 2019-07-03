package org.mholford.chatlantis.action;

import java.util.Map;

/**
 * Encapsulates user configuration of an Action.  Will usually be instantiated by
 * deserialization of chatlantis.json config.  Note that init() should be called
 * in order to reflectively create an instance of the Action class.  This is done
 * automatically by BotConfig during its initialization.
 */
public class ActionConfig {
  private String name;
  private String cls;
  private Map<String, String> props;
  
  /**
   * Creates an instance of the Action reference in config, using reflection.
   * @return An instance of the Action class
   * @throws ReflectiveOperationException If could not create an instance of the Action class
   */
  public Action init() throws ReflectiveOperationException {
    Class<Action> ac = (Class<Action>) Class.forName(cls);
    Action a = ac.newInstance();
    a.init(props);
    return a;
  }
  
  /**
   * Gets the name of the Action
   * @return name of Action
   */
  public String getName() {
    return name;
  }
  
  /**
   * Sets the name of the Action to the specified
   * @param name name of Action
   */
  public void setName(String name) {
    this.name = name;
  }
  
  /**
   * Gets the fully-qualified class name of the Action class to be instantiated by init().
   * @return Class name
   */
  public String getCls() {
    return cls;
  }
  
  /**
   * Sets the fully-qualified class name of the Action class to be instantiated to the
   * specified value.
   * @param cls Class name
   */
  public void setCls(String cls) {
    this.cls = cls;
  }
  
  /**
   * Gets the property map used to initialize the Action
   * @return String-String map of properties
   */
  public Map<String, String> getProps() {
    return props;
  }
  
  /**
   * Sets the property map used to initialize the Action to the specified value
   * @param props Map of properties
   */
  public void setProps(Map<String, String> props) {
    this.props = props;
  }
}
