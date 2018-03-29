package util;

public class CustomObjectUtils {

  /**
   * Returns true if all arguments are non-null. False if one of them is.
   * @param args: Objects to perform nonNullCheck on.
   * @return true if all arguments are non-null. False if one of them is.
   */
  public static boolean nonNullCheck(Object... args) {
    for (Object object : args) {
      if (object == null)
        return false;
    }
    // All objects are non-null
    return true;

  }

}
