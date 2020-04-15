import sbt._

object Dependencies {
  object Plugin {
    val silencerPlugin          = "com.github.ghik"       %% "silencer-plugin"    % "1.4.2"
    val silencer                = "com.github.ghik"       %% "silencer-lib"       % "1.4.2" % Provided
  }

  val zio         = "dev.zio"        %% "zio"          % "1.0.0-RC18-2"
  val zioProcess  = "dev.zio"        %% "zio-process"  % "0.0.2"
  val zioStream   = "dev.zio"        %% "zio-streams"  % "1.0.0-RC18-2"

  val scalatest  = "org.scalatest"  %% "scalatest"  % "3.1.0"
  val scalacheck = "org.scalacheck" %% "scalacheck" % "1.14.2"
}
