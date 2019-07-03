package org.mholford.chatlantis.context;

/**
 * Exception class to handle an exception in Context Traversal.
 * This is a form of Runtime exception.
 */
public class ContextTraversalException extends RuntimeException {
  public ContextTraversalException(String message) {
    super(message);
  }
  
  /**
   * Constructs a new exception
   * @param message Message to show
   * @param cause Underlying cause of exception
   */
  public ContextTraversalException(String message, Throwable cause) {
    super(message, cause);
  }
}
