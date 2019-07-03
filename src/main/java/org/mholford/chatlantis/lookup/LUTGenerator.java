package org.mholford.chatlantis.lookup;

import java.io.IOException;

/**
 * Interface to be implemented by classes which build am FST lookup table.
 * The generate() method should create a file that can be read in when
 * populating the FST dictionary.
 */
public interface LUTGenerator {
  /**
   * Generate the lookup table
   * @throws IOException If something went wrong
   */
  void generate() throws IOException;
}
