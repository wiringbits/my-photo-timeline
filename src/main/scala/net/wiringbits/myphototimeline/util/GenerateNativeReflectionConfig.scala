package net.wiringbits.myphototimeline.util

/**
 * Just a helper to generate the config file to include reflection related classes
 * on the native image.
 */
object GenerateNativeReflectionConfig {

  def metadataExtractorReflectConfig: String = {
    List(
      comDrewLang,
      comDrewMetadataExif,
      comDrewMetadataExifMakernotes
    ).flatten.mkString("[", ",\n", "]")
  }

  private def comDrewMetadataExifMakernotes: List[String] = {
    val pack = "com.drew.metadata.exif.makernotes"
    val classes =
      """AppleMakernoteDescriptor
        |AppleMakernoteDirectory
        |AppleRunTimeMakernoteDescriptor
        |AppleRunTimeMakernoteDirectory
        |CanonMakernoteDescriptor
        |CanonMakernoteDirectory
        |CasioType1MakernoteDescriptor
        |CasioType1MakernoteDirectory
        |CasioType2MakernoteDescriptor
        |CasioType2MakernoteDirectory
        |FujifilmMakernoteDescriptor
        |FujifilmMakernoteDirectory
        |KodakMakernoteDescriptor
        |KodakMakernoteDirectory
        |KyoceraMakernoteDescriptor
        |KyoceraMakernoteDirectory
        |LeicaMakernoteDescriptor
        |LeicaMakernoteDirectory
        |LeicaType5MakernoteDescriptor
        |LeicaType5MakernoteDirectory
        |NikonType1MakernoteDescriptor
        |NikonType1MakernoteDirectory
        |NikonType2MakernoteDescriptor
        |NikonType2MakernoteDirectory
        |OlympusCameraSettingsMakernoteDescriptor
        |OlympusCameraSettingsMakernoteDirectory
        |OlympusEquipmentMakernoteDescriptor
        |OlympusEquipmentMakernoteDirectory
        |OlympusFocusInfoMakernoteDescriptor
        |OlympusFocusInfoMakernoteDirectory
        |OlympusImageProcessingMakernoteDescriptor
        |OlympusImageProcessingMakernoteDirectory
        |OlympusMakernoteDescriptor
        |OlympusMakernoteDirectory
        |OlympusRawDevelopment2MakernoteDescriptor
        |OlympusRawDevelopment2MakernoteDirectory
        |OlympusRawDevelopmentMakernoteDescriptor
        |OlympusRawDevelopmentMakernoteDirectory
        |OlympusRawInfoMakernoteDescriptor
        |OlympusRawInfoMakernoteDirectory
        |PanasonicMakernoteDescriptor
        |PanasonicMakernoteDirectory
        |PentaxMakernoteDescriptor
        |PentaxMakernoteDirectory
        |ReconyxHyperFireMakernoteDescriptor
        |ReconyxHyperFireMakernoteDirectory
        |ReconyxUltraFireMakernoteDescriptor
        |ReconyxUltraFireMakernoteDirectory
        |RicohMakernoteDescriptor
        |RicohMakernoteDirectory
        |SamsungType2MakernoteDescriptor
        |SamsungType2MakernoteDirectory
        |SanyoMakernoteDescriptor
        |SanyoMakernoteDirectory
        |SigmaMakernoteDescriptor
        |SigmaMakernoteDirectory
        |SonyType1MakernoteDescriptor
        |SonyType1MakernoteDirectory
        |SonyType6MakernoteDescriptor
        |SonyType6MakernoteDirectory""".stripMargin

    toConfigItems(classes = classes, pack = pack)
  }

  private def comDrewMetadataExif: List[String] = {
    val pack = "com.drew.metadata.exif"
    val classes =
      """
        |ExifDescriptorBase
        |ExifDirectoryBase
        |ExifIFD0Descriptor
        |ExifIFD0Directory
        |ExifImageDescriptor
        |ExifImageDirectory
        |ExifInteropDescriptor
        |ExifInteropDirectory
        |ExifReader
        |ExifSubIFDDescriptor
        |ExifSubIFDDirectory
        |ExifThumbnailDescriptor
        |ExifThumbnailDirectory
        |ExifTiffHandler
        |GpsDescriptor
        |GpsDirectory
        |PanasonicRawDistortionDescriptor
        |PanasonicRawDistortionDirectory
        |PanasonicRawIFD0Descriptor
        |PanasonicRawIFD0Directory
        |PanasonicRawWbInfo2Descriptor
        |PanasonicRawWbInfo2Directory
        |PanasonicRawWbInfoDescriptor
        |PanasonicRawWbInfoDirectory
        |PrintIMDescriptor
        |PrintIMDirectory"""

    toConfigItems(classes = classes, pack = pack)
  }

  private def comDrewLang: List[String] = {
    val pack = "com.drew.lang"
    val classes =
      """BufferBoundsException
        |ByteArrayReader
        |ByteConvert
        |ByteTrie
        |ByteUtil
        |Charsets
        |CompoundException
        |DateUtil
        |GeoLocation
        |Iterables
        |KeyValuePair
        |NullOutputStream
        |RandomAccessFileReader
        |RandomAccessReader
        |RandomAccessStreamReader
        |Rational
        |SequentialByteArrayReader
        |SequentialReader
        |StreamReader
        |StreamUtil
        |StringUtil"""

    toConfigItems(classes = classes, pack = pack)
  }

  private def toConfigItems(classes: String, pack: String): List[String] = {
    classes.stripMargin.trim.split("\n").map(x => s"$pack.$x").map(toConfigItem).toList
  }

  private def toConfigItem(clazz: String): String = {
    s"""
      |  {
      |    "name": "$clazz",
      |    "allDeclaredConstructors": true,
      |    "allPublicConstructors": true,
      |    "allDeclaredMethods": true,
      |    "allPublicMethods": true,
      |    "allDeclaredClasses": true,
      |    "allPublicClasses": true
      |  }""".stripMargin
  }
}
