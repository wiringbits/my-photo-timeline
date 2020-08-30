lazy val root = (project in file("."))
  .enablePlugins(GitVersioning, BuildInfoPlugin, NativeImagePlugin)
  .settings(
    name := """my-photo-timeline""",
    organization := "net.wiringbits",
    scalaVersion := "2.13.3",
    fork in Test := true,
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion, git.baseVersion, git.gitHeadCommit),
    buildInfoPackage := "net.wiringbits.myphototimeline",
    buildInfoUsePackageAsPath := true,
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "os-lib" % "0.7.1",
      "com.google.guava" % "guava" % "28.0-jre",
      "com.drewnoakes" % "metadata-extractor" % "2.14.0",
      "com.monovore" %% "decline" % "1.0.0"
    ),
    Compile / mainClass := Some("net.wiringbits.myphototimeline.Main"),
    nativeImageOptions ++= List("--no-fallback")
  )
