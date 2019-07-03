package org.mholford.chatlantis.workflow;

import java.io.IOException;
import java.util.Map;

/**
 * Encapsulates user configuration of an utterance processing stage.  Is composed of a
 * fully-qualified class path to the UtteranceProcessor instance and a map of properties
 * used to initialized the Utterance Processor.  Typically, this class is instantiated
 * by deserializing from the chatlantis.json config file.  This is handled internally
 * by Chatlantis when it initializes from configs.
 */
public class UtteranceProcessorConfig {
  private String cls;
  private Map<String, String> props;
  
  /**
   * Gets the fully qualified class name of the Utterance Processor
   * @return Class name
   */
  public String getCls() {
    return cls;
  }
  
  /**
   * Sets the fully qualified class name of the Utterance Processor to the specified value
   * @param cls Class name
   */
  public void setCls(String cls) {
    this.cls = cls;
  }
  
  /**
   * Gets the property map used to initialize the Utterance Processor
   * @return Property map
   */
  public Map<String, String> getProps() {
    return props;
  }
  
  /**
   * Sets the property map used to initialize the Utterance Processor to the specified value
   * @param props Property map
   */
  public void setProps(Map<String, String> props) {
    this.props = props;
  }
  
  /**
   * Initializes the Utterance Processor by creating a new instance from the specified
   * class name.  It then attempts to initialize the processor with the specified property
   * map
   * @return Fully configured Utterance Processor
   * @throws ReflectiveOperationException If could not instantiate the processor class
   * @throws IOException If something else went wrong
   */
  public UtteranceProcessor init() throws ReflectiveOperationException, IOException {
    Class<UtteranceProcessor> upc = (Class<UtteranceProcessor>) Class.forName(cls);
    UtteranceProcessor up = upc.newInstance();
    up.init(props);
    return up;
  }
}
