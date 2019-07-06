package org.mholford.chatlantis.intent;

import java.util.Map;

/**
 * Encapsulates user configuration of an IntentResolver.  This is typically populated by
 * deserialization of the chatlantis.json config file during startup of Chatlantis.
 */
public class IntentResolverConfig {
  private String cls;
  private Map<String, String> props;
  
  /**
   * Gets the fully qualified class name of the Intent Resolver class
   * @return Class name
   */
  public String getCls() {
    return cls;
  }
  
  /**
   * Sets the fully qualified class name of the Intent Resolver class to the specified value
   * @param cls Class name
   */
  public void setCls(String cls) {
    this.cls = cls;
  }
  
  /**
   * Gets the map of properties used to initialize the Intent Resolver
   * @return Map of properties
   */
  public Map<String, String> getProps() {
    return props;
  }
  
  /**
   * Sets the map of properties used to initialize the Intent Resolver to the specified value
   * @param props Map of properties
   */
  public void setProps(Map<String, String> props) {
    this.props = props;
  }
  
  /**
   * Initializes the IntentResolver reflectively, using the supplied property map
   * @return Instantiated IntentResolver
   * @throws ReflectiveOperationException If the class could not be instantiated
   */
  public IntentResolver init() throws ReflectiveOperationException {
    Class<IntentResolver> irc = (Class<IntentResolver>) Class.forName(cls);
    IntentResolver ir = irc.newInstance();
    ir.init(props);
    return ir;
  }
  
  /**
   * Gets the default IntentResolver, which is ValidatingIntentResolver
   * @return ValidatingIntentResolver
   */
  public static IntentResolver getDefault() {
    return new ValidatingIntentResolver();
  }
}
