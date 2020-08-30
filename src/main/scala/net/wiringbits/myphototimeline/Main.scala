package net.wiringbits.myphototimeline

import cats.implicits._
import com.monovore.decline._
import net.wiringbits.myphototimeline.CommandAppHelper._

object Main
    extends CommandApp(
      name = appName,
      header = appDescription,
      main = {
        val dryRunOpt = Opts
          .flag("dry-run", help = "Print the actions to do without changing anything (recommended).")
          .orFalse

        (sourceOpt, outputOpt, dryRunOpt).mapN { (source, output, dryRun) =>
          run(source = source, output = output, dryRun = dryRun)
        }
      }
    )
