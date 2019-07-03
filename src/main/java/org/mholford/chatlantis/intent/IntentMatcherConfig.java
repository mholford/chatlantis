package org.mholford.chatlantis.intent;

import java.util.Map;

/**
 * Encapsulates user configuration of an Intent Matcher.  This is typically populated
 * by deserialization of the chatlantis.json file during initialization of the program.
 * When this objects is initialized (by calling init()), an instance of the specified
 * Intent Matcher is created reflectively.  This method is called behind the scenes
 * during initialization of Chatlantis from chatlantis.json.
 */
public class IntentMatcherConfig {
  private String cls;
  private Map<String, String> props;
  
  /**
   * Gets the fully qualified class name of the Intent Matcher
   * @return Class name
   */
  public String getCls() {
    return cls;
  }
  
  /**
   * Sets the fully qualified class name of the Intent Matcher to the specified value.
   * @param cls Class name
   */
  public void setCls(String cls) {
    this.cls = cls;
  }
  
  /**
   * Gets the map of properties used to construct the Intent Matcher
   * @return Map of properties
   */
  public Map<String, String> getProps() {
    return props;
  }
  
  /**
   * Sets the map of properties used to construct the Intent Matcher to the specified value.
   * @param props Map of properties
   */
  public void setProps(Map<String, String> props) {
    this.props = props;
  }
  
  /**
   * Initializes the Intent Matcher reflectively, using properties supplied.
   * @return Configured IntentMatcher
   * @throws ReflectiveOperationException If the class could not be created
   */
  public IntentMatcher init() throws ReflectiveOperationException {
    Class<IntentMatcher> imc = (Class<IntentMatcher>) Class.forName(cls);
    IntentMatcher im = imc.newInstance();
    im.init(props);
    return im;
  }
}
