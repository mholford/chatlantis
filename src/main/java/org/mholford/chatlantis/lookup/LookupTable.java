package org.mholford.chatlantis.lookup;

import org.mholford.chatlantis.utterance.Utterance;

import java.io.IOException;

/**
 * Table where Utterances are looked up and Context DSL statements to be
 * applied to current Context are returned.
 */
public interface LookupTable {

  /**
   * Look up the utterance in the table and answer with Context DSL statements
   * to be executed against the FullContext snapshot.  This lookup is performed
   * during standard Workflow processing.  After all Utterance variants are
   * computed, each is looked up.  The results which are most conducive toward
   * validating the current Intent are what is ultimately selected as the response.
   * @param u Utterance (after going through Utterance processing)
   * @return Context DSL statements to be executed
   * @throws IOException If something went wrong
   */
  String lookup(Utterance u) throws IOException;
}
