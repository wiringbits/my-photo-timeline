package net.wiringbits.myphototimeline

class FileOrganizerTask {

  def run(inputRoot: os.Path, outputRoot: os.Path, duplicatedRoot: os.Path, invalidRoot: os.Path): Unit = {
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

    println(s"Indexing now... it may take some minutes, be patient")
    val allFiles = filesToProcess.data.keys.foldLeft(processedFiles) {
      case (acc, currentHash) =>
        acc + filesToProcess.data.getOrElse(currentHash, List.empty)
    }

    val (newDuplicated, newUnique) =
      filesToProcess.data.values.foldLeft(List.empty[FileDetails] -> List.empty[FileDetails]) {
        case ((newDuplicated, newUnique), items) =>
          items.headOption
            .filterNot(f => processedFiles.contains(f.hash))
            .map { head =>
              // current batch has a new element, pick the first one
              (items.drop(1) ::: newDuplicated, head :: newUnique)
            }
            .getOrElse {
              // current batch repeated
              (items ::: newDuplicated, newUnique)
            }
      }

    println("Initial indexing done")
    println(s"- Unique files: ${allFiles.size}")
    println(s"- Already organized files: ${processedFiles.size}")
    println(s"- New duplicated files: ${newDuplicated.size}")
    println(s"- New unique files to organize: ${newUnique.size}")
    println()

    // Move duplicated files
    println(s"Moving duplicated files to: $duplicatedRoot")
    newDuplicated.zipWithIndex.foreach {
      case (file, index) =>
        trackProgress(current = index, total = newDuplicated.size)
        FileOrganizerService.safeMove(destinationDirectory = duplicatedRoot, sourceFile = file.source)
    }

    // Move files without metadata
    println(s"Moving invalid files to: $invalidRoot")
    invalidFilesToProcess.zipWithIndex.foreach {
      case (file, index) =>
        trackProgress(current = index, total = invalidFilesToProcess.size)
        FileOrganizerService.safeMove(destinationDirectory = invalidRoot, sourceFile = file)
    }

    println(s"Organizing unique files to: $outputRoot")
    newUnique.zipWithIndex.foreach {
      case (file, index) =>
        trackProgress(current = index, total = newDuplicated.size)
        FileOrganizerService.organizeByDate(
          destinationDirectory = outputRoot,
          sourceFile = file.source,
          createdOn = file.createdOn
        )
    }

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
