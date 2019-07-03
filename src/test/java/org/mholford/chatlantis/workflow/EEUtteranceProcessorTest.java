package org.mholford.chatlantis.workflow;

import org.junit.Test;
import org.mholford.chatlantis.Utils;
import org.mholford.chatlantis.utterance.Utterance;
import org.mholford.chatlantis.utterance.UtteranceFactory;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mholford.chatlantis.workflow.EEUtteranceProcessor.ALIAS_PROP;
import static org.mholford.chatlantis.workflow.EEUtteranceProcessor.DICT_FILE_PROP;

public class EEUtteranceProcessorTest implements Utils {
  
  @Test
  public void testEEUtteranceProcessor() throws IOException {
    EEUtteranceProcessor ee = new EEUtteranceProcessor();
    ee.init(stringMapOf(DICT_FILE_PROP, "employee-dict.csv", ALIAS_PROP, "EMP"));
    Utterance initU = UtteranceFactory.get().createNew("matt, joe and steve are abc employees",
        null);
    List<Utterance> utts = ee.process(initU);
    assertEquals(8, utts.size());
  }
}
