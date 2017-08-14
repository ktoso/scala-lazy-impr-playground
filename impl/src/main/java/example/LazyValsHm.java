package example;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.SwitchPoint;

import java.lang.invoke.*;


// by DarkDimius - https://github.com/DarkDimius/lazy-val-bench/blob/CallSites/src/test/java/example/LazyValsHm.java
public final class LazyValsHm {
  private final SwitchPoint sp = new SwitchPoint();


  public LazyValsHm(int value) {
  }

  private int value__;

  private final MethodHandle accessor = sp.guardWithTest(initter, getter);

  public int value() throws Throwable {
    return (int) accessor.invoke(this);
  }

  static private final MethodHandles.Lookup lookup = MethodHandles.lookup();
  static private final MethodType getterMT = MethodType.methodType(int.class, LazyValsHm.class);
  static private final MethodHandle initter = getInitter();
  static private final MethodHandle getter = getGetter();

  static private MethodHandle getGetter() {
    try {
      return lookup.findStatic(LazyValsHm.class, "_get", getterMT);
    } catch (IllegalAccessException | NoSuchMethodException e) {
    }
    return null;
  }

  static private MethodHandle getInitter() {
    try {
      return lookup.findStatic(LazyValsHm.class, "_init", getterMT);
    } catch (IllegalAccessException | NoSuchMethodException e) {
    }
    return null;
  }

  static private final int _get(LazyValsHm who) {
    return who.value__;
  }


  static private final int _init(LazyValsHm who) throws Throwable {
/*          synchronized(who.accessor) {
      if(!who.hasBeenInvalidated())
    {*/
    who.value__ = 0;
    SwitchPoint.invalidateAll(new SwitchPoint[]{who.sp});
/*        accessor.notifyAll();
      } else {
           who.accessor.wait();
      }
      }*/
    return who.value__;
  }

}
