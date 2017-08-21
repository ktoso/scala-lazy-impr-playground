package example.lzy

import sun.misc.Unsafe

import scala.annotation.switch

//noinspection ScalaUnusedSymbol
class LazyDottyStyleCell {

  import LazyDottyStyleCell.bitmap_offset

  var value_0: String = "42"
  private var bitmap = 0

  // 
  /* @static private bitmap_offset = LazyVals.getOffset(classOf[LazyCell], "bitmap") */
  def value: String = {
    var result: String = null
    var retry: Boolean = true
    val fieldId: Int = 0 // id of lazy val
    var flag: Long = 0L
    do {
      flag = LazyVals.get(this, bitmap_offset)
      (LazyVals.STATE(flag, 0): @switch) match {
        case 0 => // 00: not-initialized
          if (LazyVals.CAS(this, bitmap_offset, flag, 1, 1)) {
            try {
              result = "42" // actually initialize the value
            } catch {
              case x: Throwable =>
                LazyVals.setFlag(this, bitmap_offset, 0, fieldId)
                throw x
            }
            value_0 = result
            LazyVals.setFlag(this, bitmap_offset, 3, fieldId)
            retry = false
          }
        case 3 => // 11: "initialized"
          retry = false
          result = value_0
        case _ => // 01 and 10: "initializing"
          LazyVals.wait4Notification(this, bitmap_offset, flag, fieldId)
      }
    } while (retry) 
    result
  }

}

object LazyDottyStyleCell {
  private val bitmap_offset = LazyVals.getOffset(classOf[LazyDottyStyleCell], "bitmap")

}

/**
 * Based on Dotty impl from:
 * https://raw.githubusercontent.com/lampepfl/dotty/master/library/src/dotty/runtime/LazyVals.scala 
 */
object LazyVals {
  private val unsafe: Unsafe = example.TheUnsafe.instance

  /* 
  * The state machine we're modeling for each value has 3 states:
  * 00 - un-unitialized
  * 01 - initializing
  * 10 - initializing
  * 10 - ready
  */
  final val BITS_PER_LAZY_VAL = 2L
  final val LAZY_VAL_MASK = 3L
  final val debug = false

  @inline def STATE(cur: Long, ord: Int): Int = {
    val r = (cur >> (ord * BITS_PER_LAZY_VAL)) & LAZY_VAL_MASK
    if (debug) println(s"STATE($cur, $ord) = $r")
    r.toInt // we want to operate on `int`, since then we'll generate a more efficient switch (I THINK - TODO check this)
  }

  @inline def CAS(t: Object, offset: Long, e: Long, v: Int, ord: Int) = {
    if (debug) println(s"CAS($t, $offset, $e, $v, $ord)")
    val mask = ~(LAZY_VAL_MASK << ord * BITS_PER_LAZY_VAL)
    val n = (e & mask) | (v << (ord * BITS_PER_LAZY_VAL))
    compareAndSet(t, offset, e, n)
  }

  @inline def setFlag(t: Object, offset: Long, v: Int, ord: Int) = {
    if (debug) println(s"setFlag($t, $offset, $v, $ord)")
    var retry = true
    while (retry) {
      val cur = get(t, offset)
      if (STATE(cur, ord) == 1) retry = CAS(t, offset, cur, v, ord)
      else {
        // cur == 2, somebody is waiting on monitor
        if (CAS(t, offset, cur, v, ord)) {
          val monitor = getMonitor(t, ord)
          monitor.synchronized {
            monitor.notifyAll()
          }
          retry = false
        }
      }
    }
  }

  @inline def wait4Notification(t: Object, offset: Long, cur: Long, ord: Int) = {
    if (debug)
      println(s"wait4Notification($t, $offset, $cur, $ord)")
    var retry = true
    while (retry) {
      val cur = get(t, offset)
      val state = STATE(cur, ord)
      if (state == 1) CAS(t, offset, cur, 2, ord)
      else if (state == 2) {
        val monitor = getMonitor(t, ord)
        monitor.synchronized {
          monitor.wait()
        }
      }
      else retry = false
    }
  }

  @inline def compareAndSet(t: Object, off: Long, e: Long, v: Long) = unsafe.compareAndSwapLong(t, off, e, v)

  @inline def get(t: Object, off: Long) = {
    if (debug)
      println(s"get($t, $off)")
    unsafe.getLongVolatile(t, off)
  }

  val processors: Int = java.lang.Runtime.getRuntime.availableProcessors()
  val base: Int = 8 * processors * processors
  val monitors: Array[Object] = (0 to base).map {
    x => new Object()
  }.toArray

  @inline def getMonitor(obj: Object, fieldId: Int = 0) = {
    var id = (
      /*java.lang.System.identityHashCode(obj) + */
      // should be here, but #548
      fieldId) % base

    if (id < 0) id += base
    monitors(id)
  }

  @inline def getOffset(clz: Class[_], name: String) = {
    val r = unsafe.objectFieldOffset(clz.getDeclaredField(name))
    if (debug)
      println(s"getOffset($clz, $name) = $r")
    r
  }

  object Names {
    final val state = "STATE"
    final val cas = "CAS"
    final val setFlag = "setFlag"
    final val wait4Notification = "wait4Notification"
    final val compareAndSet = "compareAndSet"
    final val get = "get"
    final val getOffset = "getOffset"
  }

}

