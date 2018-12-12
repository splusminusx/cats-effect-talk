name := "cats-effect-talk"

version := "0.1"

scalaVersion := "2.12.6"

libraryDependencies ++= Seq(
    "org.typelevel" %% "cats-effect" % "1.1.0"
  , "org.rocksdb" % "rocksdbjni" % "5.17.2"
  , "com.github.finagle" %% "finchx-core" % "0.26.1"
  , "com.github.finagle" %% "finchx-circe" % "0.26.1"
  , "io.circe" %% "circe-generic" % "0.10.1"
  , "io.circe" %% "circe-parser" % "0.10.1"
)

scalacOptions in ThisBuild ++= Seq(
  "-language:_",
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-unchecked",
  "-Ywarn-dead-code",
  "-Xfatal-warnings",
  "-Yno-adapted-args",
  "-Ypartial-unification",
  "-language:postfixOps",
  "-language:higherKinds",
)