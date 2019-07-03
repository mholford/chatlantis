package org.mholford.chatlantis.lookup;

import java.io.IOException;
import java.util.Map;

/**
 * Encapsulates user configuration of an FST lookup table.  This is
 * handled during initialization of Chatlantis from the chatlantis.json
 * configuration file.
 */
public class FSTLookupTableConfig {
  private Map<String, String> props;
  
  /**
   * Gets the properties used to construct the FST
   * @return Property map
   */
  public Map<String, String> getProps() {
    return props;
  }
  
  /**
   * Sets the properties used to construct the FST to the specified
   * @param props Property map
   */
  public void setProps(Map<String, String> props) {
    this.props = props;
  }
  
  /**
   * Initializes the FST from properties.  This is called during initialization
   * of Chatlantis from configs.
   * @return Populated FST lookup table
   * @throws IOException If something went wrong
   */
  public FSTLookupTable init() throws IOException {
    FSTLookupTable fst =  new FSTLookupTable();
    fst.init(props);
    return fst;
  }
}
