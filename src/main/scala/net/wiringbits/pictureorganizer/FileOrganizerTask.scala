package net.wiringbits.pictureorganizer

import scala.util.control.NonFatal

class FileOrganizerTask {

  def run(inputRoot: os.Path, outputRoot: os.Path): Unit = {
    println("Loading already processed files, it may take some minutes, be patient")
    val (processedFiles, invalidProcessedFiles) = FileOrganizerService.load(outputRoot)(trackProgress)
    println(s"Already processed files loaded: ${processedFiles.size}")
    if (invalidProcessedFiles.nonEmpty) {
      println(
        s"WARNING: There are ${invalidProcessedFiles.size} files on the output folder without enough metadata to process, which you need to organize manually"
      )
    }

    println("Loading files to process, it may take some minutes, be patient")
    val (filesToProcess, invalidFilesToProcess) = FileOrganizerService.load(inputRoot)(trackProgress)
    println(s"Files to process loaded: ${filesToProcess.size}")
    if (invalidFilesToProcess.nonEmpty) {
      println(
        s"WARNING: There are ${invalidFilesToProcess.size} files on the input folder without enough metadata to process"
      )
    }

    println(s"Processing now... it may take some minutes, be patient")
    val allFiles = filesToProcess.data.keys.foldLeft(processedFiles) {
      case (acc, currentHash) =>
        acc + filesToProcess.data.getOrElse(currentHash, List.empty)
    }

    val totalFiles = allFiles.data.values.map(_.size).sum
    val uniqueFiles = allFiles.size
    val duplicated = totalFiles - uniqueFiles

    println("Initial indexing done")
    println(s"- Unique files: $uniqueFiles")
    println(s"- Already organized files: ${processedFiles.size}")
    println(s"- Duplicated files: $duplicated")
    println(s"- New unique files to organize: ${uniqueFiles - processedFiles.size}")
    println()

    println("Organizing files now")
    filesToProcess.data.keys.zipWithIndex.foreach {
      case (hash, index) =>
        trackProgress(current = index, total = filesToProcess.size)
        filesToProcess.data
          .getOrElse(hash, List.empty)
          .headOption
          .filterNot(f => processedFiles.contains(f.hash))
          .foreach { file =>
            try {
              FileOrganizerService.organizeByDate(
                destinationDirectory = outputRoot,
                sourceFile = file.source,
                createdOn = file.createdOn
              )
            } catch {
              case NonFatal(ex) =>
                println(s"Failed to organize ${file.source}, error = ${ex.getMessage}")
            }
          }
    }

    // TODO: Move duplicated files
    // TODO: Move files without metadata
    // TODO: Move unique files that aren't in the output directory

    println("Cleaning up empty directories")
    FileOrganizerService.cleanEmptyDirectories(inputRoot)
    FileOrganizerService.cleanEmptyDirectories(outputRoot)
    println("Done")
  }

  private def trackProgress(current: Int, total: Int): Unit = {
    def percent(x: Int): Int = {
      (100 * (x * 1.0 / total)).toInt
    }
    if (current > 0) {
      val currentPercent = percent(current)
      val previous = percent(current - 1)
      if (currentPercent > previous && currentPercent % 5 == 0) {
        println(s"Progress: $currentPercent%")
      }
    }
  }
}
