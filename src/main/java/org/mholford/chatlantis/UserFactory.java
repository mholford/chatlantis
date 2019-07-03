package org.mholford.chatlantis;

import org.mholford.chatlantis.context.ContextFactory;

/**
 * Singleton factory class for creating new Chatlantis users.  New
 * users are given a fresh Context object when they are created.
 * Creation of a new User is typically handled by the Chatlantis
 * class when a statement from an unrecognized User appears.
 */
public class UserFactory {
  private static UserFactory INSTANCE;
  
  private UserFactory() {
  
  }
  
  /**
   * Gets the singleton factory object, creating it if necessary
   * @return Factory object
   */
  public static UserFactory get() {
    if (INSTANCE == null) {
      INSTANCE = new UserFactory();
    }
    return INSTANCE;
  }
  
  /**
   * Creates a new user with the specified name.  A new Context will
   * be assigned for the User
   * @param name user name
   * @return Configured user
   */
  public User createNewUser(String name) {
    return new User(name, ContextFactory.get().newContext());
  }
}
