name := """my-photo-timeline"""
organization := "net.wiringbits"
scalaVersion := "2.13.3"

fork in Test := true

lazy val root = (project in file("."))
  .enablePlugins(GitVersioning, BuildInfoPlugin)
  .settings(
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion, git.baseVersion, git.gitHeadCommit),
    buildInfoPackage := "net.wiringbits.myphototimeline",
    buildInfoUsePackageAsPath := true
  )

val bouncycastle = "1.62"

libraryDependencies += "com.lihaoyi" %% "os-lib" % "0.7.1"
libraryDependencies += "com.google.guava" % "guava" % "28.0-jre"
libraryDependencies += "com.drewnoakes" % "metadata-extractor" % "2.14.0"
libraryDependencies += "com.monovore" %% "decline" % "1.0.0"
