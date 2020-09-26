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

        val debugOpt = Opts
          .flag(
            "debug",
            help =
              "Print noisy debug logs to diagnose potential bugs (recommended if you feel the app doesn't work as expected)."
          )
          .orFalse

        (sourceOpt, outputOpt, dryRunOpt, debugOpt).mapN { (source, output, dryRun, debug) =>
          run(source = source, output = output, dryRun = dryRun, debug = debug)
        }
      }
    )
