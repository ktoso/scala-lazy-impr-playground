package example.lzy

class LazyNotSafeCell {
  
  // lazy val value: String = "42"
  
  private var _value: String = _
  
  private def lzy$value = {
    if (_value == null) _value = "42"
    _value 
  }
  
  def value: String = {
    lzy$value
  }

}
