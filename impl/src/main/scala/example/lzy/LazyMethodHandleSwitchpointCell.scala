package example.lzy

import java.lang.invoke._

class LazyMethodHandleSwitchpointCell {
  private final val sp = new SwitchPoint()


  // lazy val value = "42"
  private var _value: String = _
  private var lzy$bitmap = 0

  final val handles = MethodHandles.lookup()
    final val mhType = MethodType.methodType(classOf[String], classOf[LazyMethodHandleSwitchpointCell]) 
  final val initter = handles.findVirtual(classOf[LazyMethodHandleSwitchpointCell], "_init", mhType)
  final val getter = handles.findVirtual(classOf[LazyMethodHandleSwitchpointCell], "_get", mhType)
  final val accessor = sp.guardWithTest(initter, getter)

  def value$lzy: String = {
    accessor.invoke(this).asInstanceOf[String]
  }

  private def _get(who: LazyMethodHandleSwitchpointCell): String = {
    who._value
  }
  private def _init(who: LazyMethodHandleSwitchpointCell): String = {
    who._value = "42"
    SwitchPoint.invalidateAll(Array[SwitchPoint](who.sp))
    who._value
  }

  def value: String = {
    value$lzy
  }

}

object LazyMethodHandleCellApp extends App {
  val cell = new LazyMethodHandleSwitchpointCell
  println(s"cell.value = ${cell.value}")
}
