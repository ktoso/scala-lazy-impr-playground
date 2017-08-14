package example.lzy

import org.openjdk.jcstress.annotations.Outcome.Outcomes
import org.openjdk.jcstress.annotations._
import org.openjdk.jcstress.infra.results.LL_Result

@JCStressTest
@Description("Simple test, checking AtomicInteger")
@Outcomes(Array(
    new Outcome(
      id = Array("42, 42"), 
      expect = Expect.ACCEPTABLE, 
      desc = "Both actors came up with the same value."),
    new Outcome(
      id = Array("42, 2"), 
      expect = Expect.FORBIDDEN, 
      desc = "actor1, then actor2.")
  )
)
@State
class LazyVarHandleStress {

  val cell = new example.lzy.LazyVarHandleCell

  @Actor
  def actor1(r: LL_Result): Unit = {
    r.r1 = cell.value // record result from actor1 to field r1
  }

  @Actor
  def actor2(r: LL_Result): Unit = {
    r.r2 = cell.value // record result from actor1 to field r2
  }

}
