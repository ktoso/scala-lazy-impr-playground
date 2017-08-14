package example.lzy

import java.lang.invoke.VarHandle.AccessMode
import java.lang.invoke._

class LazyVarHandleCell {
  // lazy val value = "42"
  private var _value: String = _
  private var lzy$bitmap = 0

  val ValueHandle: VarHandle = MethodHandles.lookup().findVarHandle(classOf[LazyVarHandleCell], "_value", classOf[String])
  val BitmapHandle: VarHandle = MethodHandles.lookup().findVarHandle(classOf[LazyVarHandleCell], "lzy$bitmap", classOf[Int])

  def value$lzy: String = {
    println(s"lzy$$bitmap = ${lzy$bitmap}")
    if ((BitmapHandle.getAndBitwiseAnd(this, 1).asInstanceOf[Int] & 1) == 0) {
      println(s"lzy$$bitmap = ${lzy$bitmap}")
      
      ValueHandle.set(this, "42")
    }
    ValueHandle.get(this).asInstanceOf[String]
  }

  def value: String = {
    value$lzy
  }

}

object LazyVarHandleCellApp extends App {
  val cell = new LazyVarHandleCell
  println(s"cell.value = ${cell.value}")
}
