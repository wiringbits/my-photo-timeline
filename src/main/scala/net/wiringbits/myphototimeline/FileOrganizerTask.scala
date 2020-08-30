package net.wiringbits.myphototimeline

object FileOrganizerTask {
  case class Arguments(inputRoot: os.Path, outputBaseRoot: os.Path, dryRun: Boolean) {
    val outputRoot: os.Path = outputBaseRoot / "organized"
    val duplicatedRoot: os.Path = outputBaseRoot / "duplicated"
    val invalidRoot: os.Path = outputBaseRoot / "invalid"

    val dataDirectories: List[os.Path] = List(
      inputRoot,
      outputRoot,
      duplicatedRoot,
      invalidRoot
    )
  }
}

class FileOrganizerTask {

  import FileOrganizerTask._

  def run(args: Arguments): Unit = {
    validate(args)

    println("Loading already processed files, it may take some minutes, be patient")
    val (processedFiles, invalidProcessedFiles) = FileOrganizerService.load(args.outputRoot)(trackProgress)
    println(s"Already processed files loaded: ${processedFiles.size}")
    if (invalidProcessedFiles.nonEmpty) {
      println(
        s"WARNING: There are ${invalidProcessedFiles.size} files on the output folder without enough metadata to process, which you need to organize manually"
      )
    }

    println("Loading files to process, it may take some minutes, be patient")
    val (filesToProcess, invalidFilesToProcess) = FileOrganizerService.load(args.inputRoot)(trackProgress)
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

    if (args.dryRun) {
      println("Files not affected because dry-run is enabled")
    } else {
      // Move duplicated files
      println(s"Moving duplicated files to: ${args.duplicatedRoot}")
      newDuplicated.zipWithIndex.foreach {
        case (file, index) =>
          trackProgress(current = index, total = newDuplicated.size)
          FileOrganizerService.safeMove(destinationDirectory = args.duplicatedRoot, sourceFile = file.source)
      }

      // Move files without metadata
      println(s"Moving invalid files to: ${args.invalidRoot}")
      invalidFilesToProcess.zipWithIndex.foreach {
        case (file, index) =>
          trackProgress(current = index, total = invalidFilesToProcess.size)
          FileOrganizerService.safeMove(destinationDirectory = args.invalidRoot, sourceFile = file)
      }

      println(s"Organizing unique files to: ${args.outputRoot}")
      newUnique.zipWithIndex.foreach {
        case (file, index) =>
          trackProgress(current = index, total = newDuplicated.size)
          FileOrganizerService.organizeByDate(
            destinationDirectory = args.outputRoot,
            sourceFile = file.source,
            createdOn = file.createdOn
          )
      }

      println("Cleaning up empty directories")
      FileOrganizerService.cleanEmptyDirectories(args.inputRoot)
      FileOrganizerService.cleanEmptyDirectories(args.outputRoot)
    }

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

  private def exit(msg: String): Unit = {
    println(s"FATAL: $msg")
    sys.exit(1)
  }

  private def validateDirectory(path: os.Path): Unit = {
    try {
      lazy val exists = os.exists(path)
      lazy val isDir = os.isDir(path)
      if (isDir) {
        ()
      } else if (!exists) {
        os.makeDir.all(path)
      } else {
        exit(s"$path is not a directory, or it can't be created")
      }
    } catch {
      case ex: Throwable =>
        ex.printStackTrace()
        exit(s"$path is not a directory, or it can't be created")
    }
  }

  private def validate(args: Arguments): Unit = {
    args.dataDirectories.foreach(validateDirectory)

    if (args.outputRoot.toString().startsWith(args.inputRoot.toString())) {
      exit("The output directory can't be inside the input directory")
    }

    if (args.inputRoot.toString().startsWith(args.outputRoot.toString())) {
      exit("The input directory can't be inside the output directory")
    }
  }
}
