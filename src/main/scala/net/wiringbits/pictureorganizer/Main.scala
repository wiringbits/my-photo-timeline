package net.wiringbits.pictureorganizer

import java.time.LocalDate

import com.drew.imaging.ImageMetadataReader

import scala.annotation.tailrec
import scala.jdk.CollectionConverters._

object Main {

  def main(args: Array[String]): Unit = {
    val output = os.pwd / "output"
    val input = os.pwd / "test-data"
    new FileOrganizerTask().run(inputRoot = input, outputRoot = output)
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
