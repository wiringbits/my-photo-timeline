package net.wiringbits.myphototimeline

import java.nio.file.Path

import cats.data.Validated
import com.drew.imaging.ImageMetadataReader
import com.monovore.decline.Opts

import scala.jdk.CollectionConverters.IterableHasAsScala

object CommandAppHelper {

  val appName: String = BuildInfo.name

  val appDescription: String = {
    s"""
      |My Photo Timeline
      |
      |Version: ${BuildInfo.version}
      |Git head: ${BuildInfo.gitHeadCommit.getOrElse("none")}""".stripMargin
  }

  val sourceOpt: Opts[os.Path] = Opts
    .option[Path](
      long = "source",
      help = "The root directory to pick the photos to organize recursively (absolute path)."
    )
    .mapValidated { nativePath =>
      // for now, only absolute paths
      try {
        val path = os.Path(nativePath)
        if (os.isDir(path)) {
          Validated.valid(path)
        } else {
          Validated.invalidNel("source: It's not a directory, an absolute path is required")
        }
      } catch {
        case ex: Throwable =>
          Validated.invalidNel(ex.getMessage)
      }
    }

  val outputOpt: Opts[os.Path] = Opts
    .option[Path](
      long = "output",
      help = "The root directory to place the organized photos (absolute path)"
    )
    .mapValidated { nativePath =>
      // for now, only absolute paths
      try {
        val path = os.Path(nativePath)
        val exists = os.exists(path)
        val isDir = os.isDir(path)
        if (!exists || isDir) {
          Validated.valid(path)
        } else {
          Validated.invalidNel("output: It's not a directory, an absolute path is required")
        }
      } catch {
        case ex: Throwable =>
          Validated.invalidNel(ex.getMessage)
      }
    }

  def run(source: os.Path, output: os.Path, dryRun: Boolean): Unit = {
    new FileOrganizerTask().run(
      inputRoot = source,
      outputBaseRoot = output,
      dryRun = dryRun
    )
  }

  def findPotentialDate(sourceFile: os.Path): Set[String] = {
    def f = {
      val metadata = ImageMetadataReader.readMetadata(sourceFile.toIO)
      metadata.getDirectories.asScala.flatMap { d =>
        d.getTags.asScala
          .filterNot { t =>
            MetadataCreatedOnTag.names.contains(t.getTagName.toLowerCase)
          }
          .filter(_.getTagName.toLowerCase.contains("date"))
          .map { t =>
            t.getTagName
          }
      }.toSet
    }

    try f
    catch {
      case _: Throwable => Set.empty
    }
  }
}
