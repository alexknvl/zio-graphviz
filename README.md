# zio-graphviz

Simple Graphviz API for ZIO.

## Usage

```sbtshell
resolvers += Resolver.bintrayRepo("alexknvl", "maven")
libraryDependencies += "com.alexknvl"  %%  "zio-graphviz" % "0.1.0"
// Currently available only for Scala 2.12 and 2.13
```

```scala
import zio.ZIO
import zio.drawing.graphviz.Graphviz
import zio.drawing.graphviz.Graphviz.{ Engine, OutputFormat, StartLocations }

val r1: ZIO[Any, Nothing, String] = 
  Graphviz.run(
    "digraph G { a -> b; b -> c }",
    Engine.Neato(
      start = Some(StartLocations.Seeded(42)),
      maxIterations = Some(1000),
      epsilon = Some(0.001)),
    OutputFormat.SVG)
```