package zio.drawing.graphviz

import org.scalatest.PropSpec
import zio.drawing.graphviz.Graphviz._

class Tests extends PropSpec {
  val rts = zio.Runtime.default
  property("test") {
    println(rts.unsafeRun(Graphviz.version))
  }

  property("test run") {
    println(rts.unsafeRun(
      Graphviz.run(
        "digraph G { a -> b; b -> c }",
        Engine.Neato(
          start = Some(StartLocations.Seeded(42)),
          maxIterations = Some(1000),
          epsilon = Some(0.001)),
        OutputFormat.SVG)
    ))
  }
}
