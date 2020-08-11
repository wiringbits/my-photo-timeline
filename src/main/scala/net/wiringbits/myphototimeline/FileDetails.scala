package net.wiringbits.myphototimeline

import java.time.LocalDate

case class FileDetails(source: os.Path, createdOn: LocalDate, hash: String)
