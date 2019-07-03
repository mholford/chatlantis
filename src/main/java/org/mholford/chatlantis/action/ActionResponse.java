package org.mholford.chatlantis.action;

import org.mholford.chatlantis.context.FullContext;

/**
 * Represents a response to an Action.  It contains two parts: a spokenResponse to
 * be uttered next by the Bot, and a Context snapshot which will include any changes
 * made while executing the Action.
 */
public class ActionResponse {
  private final String spokenResponse;
  private final FullContext context;
  
  ActionResponse(String spokenResponse, FullContext context) {
    this.spokenResponse = spokenResponse;
    this.context = context;
  }
  
  public String getSpokenResponse() {
    return spokenResponse;
  }
  
  public FullContext getContext() {
    return context;
  }
}
