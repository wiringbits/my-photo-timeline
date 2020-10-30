package net.wiringbits.myphototimeline

object FileOrganizerTask {

  case class Arguments(inputRoot: os.Path, outputBaseRoot: os.Path, dryRun: Boolean, debug: Boolean) {
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

  trait TaskHelper[T] {
    def pre(): T
    def run(): Unit
    def print(): Unit
    def clean(): Unit
    protected def logger: SimpleLogger

    protected def trackProgress(current: Int, total: Int): Unit = {
      def percent(x: Int): Int = {
        (100 * (x * 1.0 / total)).toInt
      }
      if (current > 0) {
        val currentPercent = percent(current)
        val previous = percent(current - 1)
        if (currentPercent > previous && currentPercent % 5 == 0) {
          logger.info(fansi.Color.Blue(s"Progress: $currentPercent%").render)
        }
      }
    }
  }

  class ProcessedFilesHelper(
      outputRoot: os.Path,
      invalidRoot: os.Path,
      fileOrganizerService: FileOrganizerService
  )(implicit
      val logger: SimpleLogger
  ) extends TaskHelper[IndexedFiles] {
    var invalidProcessedFilesVar: Option[List[os.Path]] = None

    lazy val pre: IndexedFiles = {
      logger.info("Loading already processed files, it may take some minutes, be patient")
      val (processedFiles, invalidProcessedFiles) = fileOrganizerService.load(outputRoot)(trackProgress)
      logger.info(s"Already processed files loaded: ${processedFiles.size}")
      if (invalidProcessedFiles.nonEmpty) {
        logger.warn(
          s"There are ${invalidProcessedFiles.size} files on the output folder without enough metadata to process, which you need to organize manually"
        )
      }
      invalidProcessedFilesVar = Some(invalidProcessedFiles)
      processedFiles
    }

    def print(): Unit = {
      logger.info(s"- Already organized files: ${pre.size}")
    }

    def run(): Unit = {
      invalidProcessedFilesVar.fold {
        logger.fatal(s"Process.run() get called before pre this is a programming error!")
      } { invalidFilesToProcess =>
        // Move files without metadata
        logger.info(s"Moving invalid files to: $invalidRoot")
        invalidFilesToProcess.zipWithIndex.foreach { case (file, index) =>
          trackProgress(current = index, total = invalidFilesToProcess.size)
          fileOrganizerService.safeMove(destinationDirectory = invalidRoot, sourceFile = file)
        }
      }
    }

    def clean(): Unit = fileOrganizerService.cleanEmptyDirectories(outputRoot)
  }

  class NotYetProcessedFilesHelper(
      inputRoot: os.Path,
      fileOrganizerService: FileOrganizerService
  )(implicit
      val logger: SimpleLogger
  ) extends TaskHelper[IndexedFiles] {
    def run(): Unit = {}
    def print(): Unit = {}

    lazy val pre: IndexedFiles = {
      logger.info("Loading files to process, it may take some minutes, be patient")
      val (filesToProcess, invalidFilesToProcess) = fileOrganizerService.load(inputRoot)(trackProgress)
      logger.info(s"Files to process loaded: ${filesToProcess.size}")
      if (invalidFilesToProcess.nonEmpty) {
        logger.warn(
          s"There are ${invalidFilesToProcess.size} files on the input folder without enough metadata to process"
        )
      }
      filesToProcess
    }
    def clean(): Unit = fileOrganizerService.cleanEmptyDirectories(inputRoot)
  }

  class MainTaskHelper(
      duplicatedRoot: os.Path,
      outputRoot: os.Path,
      processedFiles: TaskHelper[IndexedFiles],
      filesToProcess: TaskHelper[IndexedFiles],
      fileOrganizerService: FileOrganizerService
  )(implicit
      val logger: SimpleLogger
  ) extends TaskHelper[Unit] {
    private var allFilesVar: Option[IndexedFiles] = None
    private var newDuplicatedVar: Option[List[FileDetails]] = None
    private var newUniqueVar: Option[List[FileDetails]] = None

    private def runCheck(block: (IndexedFiles, List[FileDetails], List[FileDetails]) => Unit): Unit = {
      val result = for {
        allFiles <- allFilesVar
        newDuplicated <- newDuplicatedVar
        newUnique <- newUniqueVar
      } yield {
        block(allFiles, newDuplicated, newUnique)
      }
      result.getOrElse(logger.fatal(s"Process functions get called before pre this is a programming error!"))
    }

    def pre(): Unit = {
      logger.info(s"Indexing now... it may take some minutes, be patient")
      allFilesVar = Some(filesToProcess.pre().data.keys.foldLeft(processedFiles.pre()) { case (acc, currentHash) =>
        acc + filesToProcess.pre().data.getOrElse(currentHash, List.empty)
      })

      val (newDuplicated, newUnique) =
        filesToProcess.pre().data.values.foldLeft(List.empty[FileDetails] -> List.empty[FileDetails]) {
          case ((newDuplicated, newUnique), items) =>
            items.headOption
              .filterNot(f => processedFiles.pre().contains(f.hash))
              .map { head =>
                // current batch has a new element, pick the first one
                (items.drop(1) ::: newDuplicated, head :: newUnique)
              }
              .getOrElse {
                // current batch repeated
                (items ::: newDuplicated, newUnique)
              }
        }
      newDuplicatedVar = Some(newDuplicated)
      newUniqueVar = Some(newUnique)
    }

    def run(): Unit = {
      runCheck { (_, newDuplicated, newUnique) =>
        // Move duplicated files
        logger.info(s"Moving duplicated files to: ${duplicatedRoot}")
        newDuplicated.zipWithIndex.foreach { case (file, index) =>
          trackProgress(current = index, total = newDuplicated.size)
          fileOrganizerService.safeMove(destinationDirectory = duplicatedRoot, sourceFile = file.source)
        }

        processedFiles.run()

        logger.info(s"Organizing unique files to: ${outputRoot}")
        newUnique.zipWithIndex.foreach { case (file, index) =>
          trackProgress(current = index, total = newDuplicated.size)
          fileOrganizerService.organizeByDate(
            destinationDirectory = outputRoot,
            sourceFile = file.source,
            createdOn = file.createdOn
          )
        }
      }
    }

    def clean(): Unit = {
      logger.info("Cleaning up empty directories")
      filesToProcess.clean()
      processedFiles.clean()
    }

    def print(): Unit = {
      runCheck { (allFiles, newDuplicated, newUnique) =>
        logger.info("Initial indexing done")
        logger.info(s"- Unique files: ${allFiles.size}")
        processedFiles.print()
        logger.info(s"- New duplicated files: ${newDuplicated.size}")
        logger.info(s"- New unique files to organize: ${newUnique.size}")
        logger.info("")
      }
    }
  }
}

class FileOrganizerTask(fileOrganizerService: FileOrganizerService)(implicit logger: SimpleLogger) {

  import FileOrganizerTask._

  def run(args: Arguments): Unit = {
    validate(args)

    logger.debug("Debug mode enabled\n")

    val taskHelper = new MainTaskHelper(
      args.duplicatedRoot,
      args.outputRoot,
      new ProcessedFilesHelper(args.outputRoot, args.invalidRoot, fileOrganizerService),
      new NotYetProcessedFilesHelper(args.inputRoot, fileOrganizerService),
      fileOrganizerService
    )

    taskHelper.pre()
    taskHelper.print()

    if (args.dryRun) {
      logger.info("Files not affected because dry-run is enabled")
    } else {
      taskHelper.run()
      taskHelper.clean()
    }

    logger.info("Done")

    printOutro(args.dryRun)
  }

  def printOutro(dryRun: Boolean): Unit = {
    if (logger.isDebugEnabled) {
      logger.debug(
        """
          |Given that you are running in debug mode
          |It is likely that you are finding unexpected behavior.
          |
          |Feel free to copy the output to create an issue so that we can investigate and fix:
          |- https://github.com/wiringbits/my-photo-timeline/issues/new"""
      )
    }
    if (dryRun) {
      logger.info("Remember to remove the --dry-run option to actually organize the photos")
    } else {
      val text =
        """
          |I hope you found the app useful.
          |
          |When I was looking for one, I was willing to pay $100 USD for it but found nothing fulfilling my needs.
          |any donations are welcome:
          |- Bitcoin: bc1qf37j0wutmn9ngkpn8v7mknukn3f0cmvq3p7dzf
          |- Ethereum: 0x02D1f6b4992fD147F19525150b97509D2eaAa651
          |- Litecoin: LWYPqEYG6fQdvCWCKWvFygskNTptqxuUHu
          |""".stripMargin
      logger.info(text)
    }
  }

  private def exit(msg: String): Unit = {
    logger.fatal(msg)
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
