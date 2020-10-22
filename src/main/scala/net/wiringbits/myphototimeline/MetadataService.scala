package net.wiringbits.myphototimeline

import java.time.LocalDate

import com.drew.imaging.ImageMetadataReader

import scala.jdk.CollectionConverters._

class MetadataService(implicit logger: SimpleLogger) {
  import MetadataService._

  def getCreationDateFromFilename(sourceFile: os.Path): Option[LocalDate] = {
    def f = {
      sourceFile.last match {
        case filenameRegex(year, month, day) =>
          val date = LocalDate.of(year.toInt, month.toInt, day.toInt)
          Some(date)
        case _ => None
      }
    }

    try {
      val result = f
      result match {
        case Some(value) => logger.debug(s"Creation date from filename found on $sourceFile, value = $value")
        case None => logger.debug(s"Creation date from filename not found on $sourceFile")
      }

      result
    } catch {
      case ex: Throwable =>
        logger.debug(s"Failed to find creation date from filename on $sourceFile", ex)
        None
    }
  }

  def getCreationDate(sourceFile: os.Path): Option[LocalDate] = {
    def f = {
      val metadata = ImageMetadataReader.readMetadata(sourceFile.toIO)
      val dates = metadata.getDirectories.asScala.flatMap { d =>
        d.getTags.asScala
          .filter { t =>
            potentialMetadataKeys.contains(t.getTagName.toLowerCase)
          }
          .map(_.getDescription)
          .flatMap(Option.apply)
          .flatMap(toDate)
      }.toList

      dates.headOption
    }

    val result =
      try f
      catch {
        case ex: Throwable =>
          logger.debug(s"Failed to find metadata date from filename on $sourceFile", ex)
          None
      }

    result match {
      case Some(value) =>
        logger.debug(s"Creation date found on metadata from filename found on $sourceFile, value = $value")
        Some(value)
      case None =>
        logger.debug(
          s"Creation date on metadata not found on $sourceFile, falling back to a potential date on the filename"
        )
        getCreationDateFromFilename(sourceFile)
    }
  }

  def toDate(str: String): Option[LocalDate] = {
    def f = {
      str match {
        case regex(year, month, day) =>
          val date = LocalDate.of(year.toInt, month.toInt, day.toInt)
          Some(date)
        case _ => None
      }
    }

    try {
      val result = f
      result match {
        case Some(value) =>
          logger.debug(s"""Date parsed properly, date = $value, from: $str""")
        case None =>
          logger.debug(s"""Unable to parse date from: $str""")
      }
      result
    } catch {
      case ex: Throwable =>
        logger.debug(s"Failed to parse date from: $str", ex)
        None
    }
  }
}

object MetadataService {

  private val regex = """(\d\d\d\d).(\d\d).(\d\d).*""".r
  private val filenameRegex = """(\d\d\d\d)(\d\d)(\d\d)\D.*""".r

  //    val knownTags = List(
  //      "Date/Time", // 2014:08:31 14:31:24
  //      "Date/Time Original", // 2016:09:10 15:11:52
  //      "Date/Time Digitized", // 2016:09:10 15:11:52
  //      "File Modified Date", // Fri Jun 29 08:49:36 -06:00 2029
  //      "Profile Date/Time", // 1998:02:09 06:49:00
  //      "Date Created", // 2015:06:27
  //      "Digital Date Created" // 2015:06:27
  //      Creation Date - 2020-04-14T17:31:57-0600
  //    )
  val potentialMetadataKeys: List[String] = List(
    "Date/Time",
    "Date/Time Original",
    "Date/Time Digitized",
    "Date Created",
    "Digital Date Created",
    "Creation Date",
    "Profile Date/Time"
  ).map(_.toLowerCase)
}
