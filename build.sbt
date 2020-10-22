import java.io.File
import java.nio.file.{Files, StandardCopyOption}

import sbt.TupleSyntax.t2ToTable2

val copyNativeImageConfigs = taskKey[Unit]("Copy native-image configurations to target")

copyNativeImageConfigs := ((baseDirectory, target) map { (base, trg) =>
  {
    Some(new File(trg, "native-image/META-INF/native-image").toPath)
      .filterNot(p => Files.isDirectory(p))
      .foreach(p => Files.createDirectories(p))
    new File(base, "META-INF/native-image")
      .listFiles()
      .foreach(file =>
        Files.copy(
          file.toPath,
          new File(trg, s"native-image/META-INF/native-image/${file.getName}").toPath,
          StandardCopyOption.REPLACE_EXISTING
        )
      )
  }
}).value

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
      "com.lihaoyi" %% "fansi" % "0.2.9",
      "com.google.guava" % "guava" % "28.0-jre",
      "com.drewnoakes" % "metadata-extractor" % "2.14.0",
      "com.monovore" %% "decline" % "1.3.0"
    ),
    Compile / mainClass := Some("net.wiringbits.myphototimeline.Main"),
    nativeImageOptions ++= List(
      "--no-fallback",
      "-H:+AddAllCharsets"
    ),
    // To generate this file, run "sbt console",
    // and then, "net.wiringbits.myphototimeline.util.GenerateNativeReflectionConfig.generateConfigFile()"
    // That's enough to regenerated the config file.
    //
    // TODO: Generate such config automatically before building the native image
    nativeImageOptions ++= List(
      "-H:ReflectionConfigurationFiles=META-INF/native-image/metadata-extractor-reflect-config.json",
      "-H:ResourceConfigurationFiles=META-INF/native-image/resource-config.json"
    ),
    nativeImage := (nativeImage dependsOn copyNativeImageConfigs).value
  )
