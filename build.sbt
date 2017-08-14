import sbt._
import Keys._

val impl = project.in(file("impl"))

val bench = project.in(file("bench"))
  .dependsOn(impl)

val jcstress = project.in(file("jcstress"))
  .dependsOn(impl)

val root = project.in(file("."))  
  .aggregate(impl, bench, jcstress)

