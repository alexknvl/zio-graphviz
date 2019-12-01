package zio.drawing.graphviz

import org.scalatest.PropSpec
import zio.DefaultRuntime
import zio.drawing.graphviz.Graphviz._

class Tests extends PropSpec {
  property("test") {
    val rts = new DefaultRuntime {}
    println(rts.unsafeRunSync(Graphviz.version))
  }

  property("test run") {
    val rts = new DefaultRuntime {}
    println(rts.unsafeRunSync(
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
