package net.wiringbits.pictureorganizer

import java.time.LocalDate

case class FileDetails(source: os.Path, createdOn: LocalDate, hash: String)
