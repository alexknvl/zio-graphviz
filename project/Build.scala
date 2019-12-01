import sbt._

object Dependencies {
  object Plugin {
    val silencerPlugin          = "com.github.ghik"       %% "silencer-plugin"    % "1.4.2"
    val silencer                = "com.github.ghik"       %% "silencer-lib"       % "1.4.2" % Provided
  }

  val scalajHttp   = "org.scalaj"   %% "scalaj-http" % "2.4.2"

  val zio         = "dev.zio"        %% "zio"          % "1.0.0-RC17"
  val zioStream   = "dev.zio"        %% "zio-streams"  % "1.0.0-RC17"

  val jawn         = "org.typelevel" %% "jawn-ast" % "0.14.0"
  val circeCore    = "io.circe" %% "circe-core"    % "0.12.3"
  val circeParser  = "io.circe" %% "circe-parser"  % "0.12.3"

  val scalatest  = "org.scalatest"  %% "scalatest"  % "3.1.0"
  val scalacheck = "org.scalacheck" %% "scalacheck" % "1.14.2"
}
