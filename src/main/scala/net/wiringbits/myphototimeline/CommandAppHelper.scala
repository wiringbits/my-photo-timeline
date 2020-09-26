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
      |Git head: ${BuildInfo.gitHeadCommit.getOrElse("none")}
      |Bug reports: https://github.com/wiringbits/my-photo-timeline/issues
      |
      |You can collect all your photo directories in a single root directory, and just run the app (--dry-run doesn't alter your file system, it's recommended to try that first):
      |- ./my-photo-timeline --source ~/Desktop/test-photos --output ~/Desktop/test-output --dry-run
      |
      |The `test-photos` directory could look like:
      |
      |test-photos
      |├── img1.jpg
      |├── img1-again.jpg
      |├── invalid
      |│   ├── img2-no-metadata.jpg
      |├── img3.jpg
      |├── img4.jpg
      |├── img5.jpg
      |
      |Producing the `test-output` directory like:
      |
      |test-output
      |├── duplicated
      |│   ├── img1-again.jpg
      |├── invalid
      |│   ├── img2-no-metadata.jpg
      |└── organized
      |    ├── 2009
      |    │   └── 03-march
      |    │       ├── img1.jpg
      |    ├── 2010
      |    │   ├── 07-july
      |    │   │   ├── img3.jpg
      |    │   │   └── img4.jpg
      |    │   ├── 09-september
      |    │   │   ├── img5.jpg
      |
      |Where:
      |- test-output/duplicated has the photos were detected as duplicated.
      |- test-output/invalid has the photos (or non-photos) where the app couldn't detect the creation date.
      |- test-output/organized has the photos organized by date, the format being `year/month/photo-name`.
      |""".stripMargin
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

  def run(source: os.Path, output: os.Path, dryRun: Boolean, debug: Boolean): Unit = {
    val args = FileOrganizerTask.Arguments(inputRoot = source, outputBaseRoot = output, dryRun = dryRun, debug = debug)
    val logLevel = if (debug) SimpleLogger.LogLevel.Debug else SimpleLogger.LogLevel.Info
    implicit val logger = new SimpleLogger(logLevel)

    val metadataService = new MetadataService
    val fileOrganizerService = new FileOrganizerService(metadataService)
    new FileOrganizerTask(fileOrganizerService).run(args)
  }

  def findPotentialDate(sourceFile: os.Path): Set[String] = {
    def f = {
      val metadata = ImageMetadataReader.readMetadata(sourceFile.toIO)
      metadata.getDirectories.asScala.flatMap { d =>
        d.getTags.asScala
          .filterNot { t =>
            // ignore the already used metadata keys
            MetadataService.potentialMetadataKeys.contains(t.getTagName.toLowerCase)
          }
          .filter(_.getTagName.toLowerCase.contains("date")) // find potential keys including the "date" term
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
