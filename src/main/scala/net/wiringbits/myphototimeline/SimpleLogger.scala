package net.wiringbits.myphototimeline

class SimpleLogger {

  def info(msg: String): Unit = {
    System.err.println(msg)
  }

  def warn(msg: String): Unit = {
    System.err.println(s"${fansi.Color.Yellow("WARNING")}: $msg")
  }

  def fatal(msg: String): Unit = {
    System.err.println(s"${fansi.Color.Red("FATAL")}: $msg")
  }
}
