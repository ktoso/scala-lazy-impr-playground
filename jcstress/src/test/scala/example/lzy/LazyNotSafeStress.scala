package example.lzy

import org.openjdk.jcstress.annotations.Outcome.Outcomes
import org.openjdk.jcstress.annotations._
import org.openjdk.jcstress.infra.results.LL_Result

@JCStressTest
@Description("Simple test, checking AtomicInteger")
@Outcomes(Array(
    new Outcome(
      id = Array("1, 1"), 
      expect = Expect.ACCEPTABLE, 
      desc = "Both actors came up with the same value."),
    new Outcome(
      id = Array("1, 2", "2, 1"), 
      expect = Expect.ACCEPTABLE_INTERESTING, 
      desc = "Bot actors executed the initialization code, while `i` changes were visible to them")
  )
)
@State
class LazyNotSafeStress {

  var i: Int = 0
  
  val cell = new example.lzy.LazyNotSafeCell(() => {
    i += 1
    i.toString
  })

  @Actor
  def actor1(r: LL_Result): Unit = {
    r.r1 = cell.value // record result from actor1 to field r1
  }

  @Actor
  def actor2(r: LL_Result): Unit = {
    r.r2 = cell.value // record result from actor1 to field r2
  }

}
