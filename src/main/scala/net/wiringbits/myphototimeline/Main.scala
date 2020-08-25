package net.wiringbits.myphototimeline

import cats.implicits._
import com.drew.imaging.ImageMetadataReader
import com.monovore.decline._
import java.nio.file.Path

import cats.data.Validated

import scala.jdk.CollectionConverters.IterableHasAsScala

import CommandAppHelper._

object Main
    extends CommandApp(
      name = "my-photo-timeline",
      header = "My Photo Timeline",
      main = {
        val dryRunOpt = Opts
          .flag("dry-run", help = "Print the actions to do without changing anything (recommended).")
          .orFalse

        (sourceOpt, outputOpt, dryRunOpt).mapN { (source, output, dryRun) =>
          run(source = source, output = output, dryRun = dryRun)
        }
      }
    )
