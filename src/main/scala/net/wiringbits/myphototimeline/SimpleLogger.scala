package net.wiringbits.myphototimeline

class SimpleLogger(level: SimpleLogger.LogLevel) {

  import SimpleLogger._

  def info(msg: String): Unit = {
    System.err.println(msg)
  }

  def warn(msg: String): Unit = {
    System.err.println(s"${fansi.Color.Yellow("WARNING")}: $msg")
  }

  def debug(msg: => String): Unit = {
    if (level == LogLevel.Debug) {
      System.err.println(s"${fansi.Color.Green("DEBUG")}: $msg")
    }
  }

  def debug(msg: => String, ex: => Throwable): Unit = {
    if (isDebugEnabled) {
      System.err.println(s"${fansi.Color.Green("DEBUG")}: $msg")
      ex.printStackTrace(System.err)
    }
  }

  def fatal(msg: String): Unit = {
    System.err.println(s"${fansi.Color.Red("FATAL")}: $msg")
    System.err.println(
      "Please report this problem so that it gets fixed: https://github.com/wiringbits/my-photo-timeline/issues"
    )
  }

  def isDebugEnabled: Boolean = level == LogLevel.Debug
}

object SimpleLogger {
  sealed trait LogLevel

  object LogLevel {
    final case object Info extends LogLevel
    final case object Debug extends LogLevel
  }
}
