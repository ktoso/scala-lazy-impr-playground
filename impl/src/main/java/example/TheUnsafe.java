package example;

import java.lang.reflect.Field;

public class TheUnsafe {
  public static final sun.misc.Unsafe instance;

  public TheUnsafe() {
  }

  static {
    try {
      sun.misc.Unsafe found = null;
      Field[] arr$ = sun.misc.Unsafe.class.getDeclaredFields();
      int len$ = arr$.length;

      for (int i$ = 0; i$ < len$; ++i$) {
        Field field = arr$[i$];
        if (field.getType() == sun.misc.Unsafe.class) {
          field.setAccessible(true);
          found = (sun.misc.Unsafe) field.get((Object) null);
          break;
        }
      }

      if (found == null) {
        throw new IllegalStateException("Can't find instance of sun.misc.Unsafe");
      } else {
        instance = found;
      }
    } catch (Throwable var5) {
      throw new ExceptionInInitializerError(var5);
    }
  }

}
