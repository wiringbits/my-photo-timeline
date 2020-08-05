package net.wiringbits.pictureorganizer

case class IndexedFiles(data: Map[String, List[FileDetails]]) {

  def +(fileDetails: FileDetails): IndexedFiles = {
    val newData = fileDetails :: data.getOrElse(fileDetails.hash, List.empty)
    copy(data = data + (fileDetails.hash -> newData))
  }

  def +(list: List[FileDetails]): IndexedFiles = {
    list.foldLeft(this)(_ + _)
  }

  def contains(hash: String): Boolean = {
    data.contains(hash)
  }

  def size: Int = data.size
}

object IndexedFiles {
  def empty: IndexedFiles = IndexedFiles(Map.empty)
}
