# SIP-20: Improved Lazy Vals Initialization - Playground

Contains benchmark, jcstress checks as well as various implementations of new improved `lazy val` encodings.

The follow up for this repository will be implementing the feature in the Scala compiler itself.

Note that the project may require JDK9 to run, since it contains implementations experimenting with `VarHandles`,
which could be made available as optional or under a flag in Scala if deemed useful enough.


Learn more
----------

http://docs.scala-lang.org/sips/pending/improved-lazy-val-initialization.html
