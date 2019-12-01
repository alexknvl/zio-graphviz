package zio.drawing.graphviz

import java.io.{ByteArrayOutputStream, File, FileInputStream, FileOutputStream, IOException, InputStream}

import zio.{Chunk, Task, ZIO}

object Graphviz {
  // http://www.graphviz.org/content/output-formats
  sealed abstract class OutputFormat[T](val name: String, val read: File => ZIO[Any, IOException, T])
  object OutputFormat {
    final case object DOT extends OutputFormat[String]("dot", Internal.readTextFile)
    final case object SVG extends OutputFormat[String]("svg", Internal.readTextFile)
    final case object PNG extends OutputFormat[Chunk[Byte]]("png", Internal.readBinaryFile)
  }

  sealed trait StartLocations
  object StartLocations {
    final case object Random extends StartLocations
    final case class Seeded(seed: Int) extends StartLocations
  }

  sealed abstract class Engine(val name: String) {
    def args: List[String]
  }
  object Engine {
    // https://www.graphviz.org/pdf/dotguide.pdf
    final case class Dot() extends Engine("dot") {
      val args: List[String] = Nil
    }

    // https://www.graphviz.org/pdf/neatoguide.pdf
    final case class Neato
    (start: Option[StartLocations] = None,
     epsilon: Option[Double] = None,
     maxIterations: Option[Int] = None) extends Engine("neato") {
      val args: List[String] =
        (start match {
          case None => Nil
          case Some(StartLocations.Random) => List("-Gstart=rand")
          case Some(StartLocations.Seeded(v)) => List(s"-Gstart=$v")
        }) ++ (epsilon match {
          case None => Nil
          case Some(v) => List(s"-Gepsilon=$v")
        }) ++ (maxIterations match {
          case None => Nil
          case Some(v) => List(s"-Gmaxiter=$v")
        })
    }

    final case class TwoPi() extends Engine("twopi") {
      val args: List[String] = Nil
    }
    final case class Circo() extends Engine("circo") {
      val args: List[String] = Nil
    }
    final case class Fdp() extends Engine("fdp") {
      val args: List[String] = Nil
    }
    final case class Sfdp() extends Engine("sfdp") {
      val args: List[String] = Nil
    }

    // https://www.graphviz.org/pdf/patchwork.1.pdf
    final case class Patchwork() extends Engine("patchwork") {
      val args: List[String] = Nil
    }

    // https://www.graphviz.org/pdf/osage.1.pdf
    final case class Osage() extends Engine("osage") {
      val args: List[String] = Nil
    }
  }

  object Internal {
    val TmpPrefix = "zio-graphviz"
    val TmpSuffix = ""

    def withTempFile[R, E, A](a: File => ZIO[R, E, A]): ZIO[R, E, A] = {
      // We consider tmp file creation errors as fatal.
      ZIO.bracket(
        Task.effectTotal(File.createTempFile(TmpPrefix, TmpSuffix))
      )(f => Task.effectTotal(f.delete()))(a)
    }

    def runProcess(name: String, args: List[String]): ZIO[Any, Nothing, Int] = {
      // We consider process creation / run errors as fatal.
      ZIO.effectTotal {
        val pb = new ProcessBuilder(name :: args: _*)
        val p: Process = pb.start()
        p.waitFor()
      }
    }

    lazy val DotPath = "dot"

    lazy val CmdPrefix: List[String] = {
      val osName = System.getProperty("os.name")
      if (osName == null) Nil
      else if (osName.contains("indows")) {
        List("cmd", "/c")
      } else Nil
    }

    @throws[IOException]
    def readBytes(is: InputStream): Array[Byte] = {
      val arr = new Array[Byte](8192)
      var result: Array[Byte] = null
      try {
        val buf = new ByteArrayOutputStream
        var exc: Throwable = null
        try {
          var numBytes = 0
          while ( {
            numBytes = is.read(arr, 0, arr.length)
            numBytes > 0
          }) buf.write(arr, 0, numBytes)
          result = buf.toByteArray
        } catch {
          case e: Throwable =>
            exc = e
            throw e
        } finally if (buf != null) {
          if (exc != null) try buf.close() catch {
            case e: Throwable => exc.addSuppressed(e)
          } else buf.close()
        }
      } finally is.close()
      result
    }

    def writeTextFile(file: File, text: String): ZIO[Any, IOException, Unit] =
      ZIO.bracket(Task.effect(new FileOutputStream(file)).refineToOrDie[IOException])(f => Task.effectTotal(f.close())) {
        os => Task.effect(os.write(text.getBytes("UTF-8"))).refineToOrDie[IOException]
      }

    def readTextFile(file: File): ZIO[Any, IOException, String] = ZIO.effect {
      new String(readBytes(new FileInputStream(file)), "UTF-8").trim
    }.refineToOrDie[IOException]

    def readBinaryFile(file: File): ZIO[Any, IOException, Chunk[Byte]] = ZIO.effect {
      Chunk.fromArray(readBytes(new FileInputStream(file)))
    }.refineToOrDie[IOException]
  }

  import Internal._

  def run[R](text: String, engine: Engine, out: OutputFormat[R]): ZIO[Any, Nothing, R] = {
    withTempFile { fin =>
      withTempFile { fout =>
        val args = engine.args ++ List(
          // The output format.
          s"-T${out.name}",
          // Set the layout engine.
          s"-K${engine.name}",
          // Input file name.
          fin.getAbsolutePath,
          // Output file name.
          "-o", fout.getAbsolutePath
        )

        for {
          _ <- writeTextFile(fin, text).orDie
          _ <- runProcess(DotPath, args)
          r <- out.read(fout).orDie
        } yield r
      }
    }
  }

  def version: ZIO[Any, Throwable, String] = ZIO.effect {
    val pb = new ProcessBuilder("dot", "-V")
    val p = pb.start()
    new String(readBytes(p.getErrorStream), "UTF-8").trim
  }
}
