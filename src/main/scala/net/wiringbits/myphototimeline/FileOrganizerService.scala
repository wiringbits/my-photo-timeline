package net.wiringbits.myphototimeline

import java.time.LocalDate

import com.google.common.hash.Hashing
import com.google.common.io.Files

import scala.annotation.tailrec

class FileOrganizerService(metadataService: MetadataService)(implicit logger: SimpleLogger) {

  def computeHash(source: os.Path): String = {
    Files.asByteSource(source.toIO).hash(Hashing.sha256()).toString
  }

  def cleanEmptyDirectories(root: os.Path): Unit = {
    os.walk(root)
      .filter(os.isDir)
      .sortBy(_.segmentCount)
      .reverse
      .foreach { dir =>
        if (os.list(dir).isEmpty) {
          os.remove(dir)
        }
      }
  }

  def load(root: os.Path)(trackProgress: (Int, Int) => Unit): (IndexedFiles, List[os.Path]) = {
    val input = if (os.exists(root)) os.walk(root).filter(os.isFile) else List.empty
    val total = input.size
    val (left, right) = input.zipWithIndex
      .map { case (sourceFile, index) =>
        trackProgress(index, total)

        metadataService
          .getCreationDate(sourceFile)
          .map { createdOn =>
            val hash = computeHash(sourceFile)
            FileDetails(sourceFile, createdOn, hash)
          }
          .map(Right(_))
          .getOrElse(Left(sourceFile))
      }
      .partition(_.isLeft)

    val invalid = left.flatMap(_.left.toOption)
    val valid = right.flatMap(_.toOption)
    valid.foldLeft(IndexedFiles.empty)(_ + _) -> invalid.toList
  }

  def organizeByDate(destinationDirectory: os.Path, sourceFile: os.Path, createdOn: LocalDate): Unit = {
    val year = createdOn.getYear.toString
    val monthName = createdOn.getMonth.toString.toLowerCase
    val monthNumber = createdOn.getMonthValue
    val month = "%2d-%s".format(monthNumber, monthName).replace(" ", "0")
    val parent = destinationDirectory / year / month
    val destinationFile = getAvailablePath(parent, sourceFile.baseName, sourceFile.ext)
    os.move(sourceFile, destinationFile, replaceExisting = false, createFolders = true)
    destinationFile.toIO.setLastModified(createdOn.toEpochDay)
  }

  def safeMove(destinationDirectory: os.Path, sourceFile: os.Path): Unit = {
    val destinationFile = getAvailablePath(destinationDirectory, sourceFile.baseName, sourceFile.ext)
    os.move(sourceFile, destinationFile, replaceExisting = false, createFolders = true)
  }

  @tailrec
  private def getAvailablePath(parent: os.Path, baseName: String, ext: String, suffix: Int = 0): os.Path = {
    val actualName = if (suffix == 0) s"${baseName}.${ext}" else s"${baseName}_($suffix).${ext}"
    val path = parent / actualName

    if (os.exists(path)) {
      getAvailablePath(parent, baseName, ext, suffix + 1)
    } else {
      path
    }
  }
}
