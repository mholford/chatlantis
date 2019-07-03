package org.mholford.chatlantis.context;

import org.pcollections.HashTreePMap;
import org.pcollections.PCollection;
import org.pcollections.PMap;
import org.pcollections.TreePVector;

/**
 * Contains various constants used from Context navigation and manipulation
 */
public interface ContextConstants {
  String INTENT = "intent";
  String SPOKEN_RESPONSE = "spoken_response";
  char SLASH = '/';
  char PIPE = '|';
  char DOT = '.';
  char HASH = '#';
  char QM = '?';
  PMap<String, Object> EMPTY_MAP = HashTreePMap.empty();
  PCollection EMPTY_LIST = TreePVector.empty();
}
