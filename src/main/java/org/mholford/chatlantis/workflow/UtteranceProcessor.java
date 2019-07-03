package org.mholford.chatlantis.workflow;

import org.mholford.chatlantis.utterance.Utterance;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Represents a component that takes an Utterance and processes it in some way.
 * It returns a list of resulting Utterances.  The processor can be initialized
 * from a map of properties.  This is usually configured in the chatlantis.json
 * file.
 */
public interface UtteranceProcessor {

  /**
   * Processes the specified utterance and returns a list of utterances
   * @param u Original utterance
   * @return Processed utterances
   * @throws IOException If something went wrong
   */
  List<Utterance> process(Utterance u) throws IOException;
  
  /**
   * Initializes the UtteranceProcessor from a map of properties.  By default,
   * this is a no-op.
   * @param props Property map
   * @throws IOException If something went wrong
   */
  default void init(Map<String, String> props) throws IOException {
  }
}
