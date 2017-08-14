package example.lzy

class LazyNotSafeIntCell {
  
  // lazy val value: Int = 42
  
  private var lzyval$it: Int = _
  private var lzyval$bitmap: Int = _
  
  private def lzy$it = {
    if ((lzyval$bitmap & 1) == 0) {
      lzyval$bitmap = lzyval$bitmap & 1
      lzyval$it = 42
    }
    it 
  }
  
  def it: String = {
    lzy$it
  }

}
