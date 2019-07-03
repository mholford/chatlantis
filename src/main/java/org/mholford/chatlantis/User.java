package org.mholford.chatlantis;

import org.mholford.chatlantis.context.Context;

/**
 * Represents a Chatlantis user.  The User has a name and its own Context.
 * New Users should be created by the UserFactory singleton.  This is typically
 * done by the Chatlantis class on receipt of a statement from an unknown User.
 */
public class User {
  private final String name;
  private Context context;
  
  User(String name, Context context) {
    this.name = name;
    this.context = context;
  }
  
  /**
   * Gets the name of the User
   * @return User name
   */
  public String getName() {
    return name;
  }
  
  /**
   * Gets the Context for the User
   * @return Context object
   */
  public Context getContext() {
    return context;
  }
  
  /**
   * Sets the Context for the User to the specified value
   * @param context Context object
   */
  public void setContext(Context context) {
    this.context = context;
  }
}
