package net.wiringbits.myphototimeline.util

/**
 * Just a helper to generate the config file to include reflection related classes
 * on the native image.
 *
 * TODO: We can write a script to map the jar classes to the proper config file instead,
 *       this is a very poor error-prone approach that exists just to fix the issue fast.
 */
object GenerateNativeReflectionConfig {

  def generateConfigFile(): Unit = {
    val string = metadataExtractorReflectConfig
    new java.io.PrintWriter("etc/metadata-extractor-reflect-config.json") { write(string); close() }
  }

  def metadataExtractorReflectConfig: String = {
    List(
      extra,
      comDrewLang,
      comDrewMetadataExif,
      comDrewMetadataExifMakernotes,
      comDrewMetadataAdobe,
      comDrewMetadataAvi,
      comDrewMetadataBmp,
      comDrewMetadataEps,
      comDrewMetadataFile,
      comDrewMetadataGif,
      comDrewMetadataHeif,
      comDrewMetadataHeifBoxes,
      comDrewMetadataIcc,
      comDrewMetadataIco,
      comDrewMetadataIptc,
      comDrewMetadataJfif,
      comDrewMetadataJfxx,
      comDrewMetadataJpeg,
      comDrewMetadataMov,
      comDrewMetadataMovAtoms,
      comDrewMetadataMovAtomsCanon,
      comDrewMetadataMovMedia,
      comDrewMetadataMovMetadata,
      comDrewMetadataMp3,
      comDrewMetadataMp4,
      comDrewMetadataMp4Boxes,
      comDrewMetadataMp4Media,
      comDrewMetadataPcx,
      comDrewMetadataPlist,
      comDrewMetadataPng,
      comDrewMetadataTiff,
      comDrewMetadataWav,
      comDrewMetadataWebp,
      comDrewMetadataXmp
    ).flatten.mkString("[", ",", "]")
  }

  private def extra: List[String] = {
    // TODO: Need to fix this error
    // java.util.MissingResourceException: Could not load any resource bundle by com.sun.org.apache.xerces.internal.impl.msg.SAXMessages
    List(
//      "com.sun.org.apache.xerces.internal.impl.msg.SAXMessages"
    ).map(toConfigItem)
  }
  private def comDrewMetadataAdobe: List[String] = {
    val pack = "com.drew.metadata.adobe"
    val classes = """AdobeJpegDescriptor
                    |AdobeJpegDirectory
                    |AdobeJpegReader"""

    toConfigItems(classes = classes, pack = pack)
  }

  private def comDrewMetadataAvi: List[String] = {
    val pack = "com.drew.metadata.avi"
    val classes = """AviDescriptor
                    |AviDirectory
                    |AviRiffHandler"""

    toConfigItems(classes = classes, pack = pack)
  }

  private def comDrewMetadataBmp: List[String] = {
    val pack = "com.drew.metadata.bmp"
    val classes = """BmpHeaderDescriptor
                    |BmpHeaderDirectory
                    |BmpReader"""

    toConfigItems(classes = classes, pack = pack)
  }

  private def comDrewMetadataEps: List[String] = {
    val pack = "com.drew.metadata.eps"
    val classes = """EpsDescriptor
                    |EpsDirectory
                    |EpsReader"""

    toConfigItems(classes = classes, pack = pack)
  }

  private def comDrewMetadataFile: List[String] = {
    val pack = "com.drew.metadata.file"
    val classes = """FileSystemDescriptor
                    |FileSystemDirectory
                    |FileSystemMetadataReader
                    |FileTypeDescriptor
                    |FileTypeDirectory"""

    toConfigItems(classes = classes, pack = pack)
  }

  private def comDrewMetadataGif: List[String] = {
    val pack = "com.drew.metadata.gif"
    val classes = """GifAnimationDescriptor
                    |GifAnimationDirectory
                    |GifCommentDescriptor
                    |GifCommentDirectory
                    |GifControlDescriptor
                    |GifControlDirectory
                    |GifHeaderDescriptor
                    |GifHeaderDirectory
                    |GifImageDescriptor
                    |GifImageDirectory
                    |GifReader"""

    toConfigItems(classes = classes, pack = pack)
  }

  private def comDrewMetadataHeif: List[String] = {
    val pack = "com.drew.metadata.heif"
    val classes = """HeifBoxHandler
                    |HeifBoxTypes
                    |HeifContainerTypes
                    |HeifDescriptor
                    |HeifDirectory
                    |HeifHandlerFactory
                    |HeifItemTypes
                    |HeifPictureHandler""".stripMargin

    toConfigItems(classes = classes, pack = pack)
  }

  private def comDrewMetadataHeifBoxes: List[String] = {
    val pack = "com.drew.metadata.heif.boxes"
    val classes = """AuxiliaryTypeProperty
                    |Box
                    |ColourInformationBox
                    |FileTypeBox
                    |FullBox
                    |HandlerBox
                    |ImageRotationBox
                    |ImageSpatialExtentsProperty
                    |ItemInfoBox
                    |ItemLocationBox
                    |ItemProtectionBox
                    |PixelInformationBox
                    |PrimaryItemBox""".stripMargin

    toConfigItems(classes = classes, pack = pack)
  }

  private def comDrewMetadataIcc: List[String] = {
    val pack = "com.drew.metadata.icc"
    val classes = """IccDescriptor
                    |IccDirectory
                    |IccReader""".stripMargin

    toConfigItems(classes = classes, pack = pack)
  }

  private def comDrewMetadataIco: List[String] = {
    val pack = "com.drew.metadata.ico"
    val classes = """IcoDescriptor
                    |IcoDirectory
                    |IcoReader""".stripMargin

    toConfigItems(classes = classes, pack = pack)
  }

  private def comDrewMetadataIptc: List[String] = {
    val pack = "com.drew.metadata.iptc"
    val classes = """IptcDescriptor
                    |IptcDirectory
                    |IptcReader
                    |Iso2022Converter""".stripMargin

    toConfigItems(classes = classes, pack = pack)
  }

  private def comDrewMetadataJfif: List[String] = {
    val pack = "com.drew.metadata.jfif"
    val classes = """JfifDescriptor
                    |JfifDirectory
                    |JfifReader""".stripMargin

    toConfigItems(classes = classes, pack = pack)
  }

  private def comDrewMetadataJfxx: List[String] = {
    val pack = "com.drew.metadata.jfxx"
    val classes = """JfxxDescriptor
                    |JfxxDirectory
                    |JfxxReader""".stripMargin

    toConfigItems(classes = classes, pack = pack)
  }

  private def comDrewMetadataJpeg: List[String] = {
    val pack = "com.drew.metadata.jpeg"
    val classes = """HuffmanTablesDescriptor
                    |HuffmanTablesDirectory
                    |JpegCommentDescriptor
                    |JpegCommentDirectory
                    |JpegCommentReader
                    |JpegComponent
                    |JpegDescriptor
                    |JpegDhtReader
                    |JpegDirectory
                    |JpegDnlReader
                    |JpegReader""".stripMargin

    toConfigItems(classes = classes, pack = pack)
  }

  private def comDrewMetadataMov: List[String] = {
    val pack = "com.drew.metadata.mov"
    val classes = """QuickTimeAtomHandler
                    |QuickTimeAtomTypes
                    |QuickTimeContainerTypes
                    |QuickTimeContext
                    |QuickTimeDescriptor
                    |QuickTimeDictionary
                    |QuickTimeDirectory
                    |QuickTimeHandlerFactory
                    |QuickTimeMediaHandler
                    |QuickTimeMetadataHandler""".stripMargin

    toConfigItems(classes = classes, pack = pack)
  }

  private def comDrewMetadataMovAtoms: List[String] = {
    val pack = "com.drew.metadata.mov.atoms"
    val classes = """Atom
                    |FileTypeCompatibilityAtom
                    |FullAtom
                    |HandlerReferenceAtom
                    |MediaHeaderAtom
                    |MovieHeaderAtom
                    |MusicSampleDescriptionAtom
                    |SampleDescription
                    |SampleDescriptionAtom
                    |SoundInformationMediaHeaderAtom
                    |SoundSampleDescriptionAtom
                    |SubtitleSampleDescriptionAtom
                    |TextSampleDescriptionAtom
                    |TimecodeInformationMediaAtom
                    |TimecodeSampleDescriptionAtom
                    |TimeToSampleAtom
                    |TrackHeaderAtom
                    |VideoInformationMediaHeaderAtom
                    |VideoSampleDescriptionAtom""".stripMargin

    toConfigItems(classes = classes, pack = pack)
  }

  private def comDrewMetadataMovAtomsCanon: List[String] = {
    val pack = "com.drew.metadata.mov.atoms.canon"
    val classes = """CanonThumbnailAtom""".stripMargin

    toConfigItems(classes = classes, pack = pack)
  }

  private def comDrewMetadataMovMedia: List[String] = {
    val pack = "com.drew.metadata.mov.media"
    val classes = """QuickTimeMediaDirectory
                    |QuickTimeMusicDescriptor
                    |QuickTimeMusicDirectory
                    |QuickTimeMusicHandler
                    |QuickTimeSoundDescriptor
                    |QuickTimeSoundDirectory
                    |QuickTimeSoundHandler
                    |QuickTimeSubtitleDescriptor
                    |QuickTimeSubtitleDirectory
                    |QuickTimeSubtitleHandler
                    |QuickTimeTextDescriptor
                    |QuickTimeTextDirectory
                    |QuickTimeTextHandler
                    |QuickTimeTimecodeDescriptor
                    |QuickTimeTimecodeDirectory
                    |QuickTimeTimecodeHandler
                    |QuickTimeVideoDescriptor
                    |QuickTimeVideoDirectory
                    |QuickTimeVideoHandler""".stripMargin

    toConfigItems(classes = classes, pack = pack)
  }

  private def comDrewMetadataMovMetadata: List[String] = {
    val pack = "com.drew.metadata.mov.metadata"
    val classes = """QuickTimeDataHandler
                    |QuickTimeDirectoryHandler
                    |QuickTimeMetadataDescriptor
                    |QuickTimeMetadataDirectory""".stripMargin

    toConfigItems(classes = classes, pack = pack)
  }

  private def comDrewMetadataMp3: List[String] = {
    val pack = "com.drew.metadata.mp3"
    val classes = """Mp3Descriptor
                    |Mp3Directory
                    |Mp3Reader"""

    toConfigItems(classes = classes, pack = pack)
  }

  private def comDrewMetadataMp4: List[String] = {
    val pack = "com.drew.metadata.mp4"
    val classes = """Mp4BoxHandler
                    |Mp4BoxTypes
                    |Mp4ContainerTypes
                    |Mp4Context
                    |Mp4Descriptor
                    |Mp4Dictionary
                    |Mp4Directory
                    |Mp4HandlerFactory
                    |Mp4MediaHandler"""

    toConfigItems(classes = classes, pack = pack)
  }

  private def comDrewMetadataMp4Boxes: List[String] = {
    val pack = "com.drew.metadata.mp4.boxes"
    val classes = """AudioSampleEntry
                    |Box
                    |FileTypeBox
                    |FullBox
                    |HandlerBox
                    |HintMediaHeaderBox
                    |MediaHeaderBox
                    |MovieHeaderBox
                    |SampleEntry
                    |SoundMediaHeaderBox
                    |TimeToSampleBox
                    |TrackHeaderBox
                    |UserDataBox
                    |UuidBox
                    |VideoMediaHeaderBox
                    |VisualSampleEntry"""

    toConfigItems(classes = classes, pack = pack)
  }

  private def comDrewMetadataMp4Media: List[String] = {
    val pack = "com.drew.metadata.mp4.media"
    val classes = """Mp4HintDescriptor
                    |Mp4HintDirectory
                    |Mp4HintHandler
                    |Mp4MediaDirectory
                    |Mp4MetaDescriptor
                    |Mp4MetaDirectory
                    |Mp4MetaHandler
                    |Mp4SoundDescriptor
                    |Mp4SoundDirectory
                    |Mp4SoundHandler
                    |Mp4TextDescriptor
                    |Mp4TextDirectory
                    |Mp4TextHandler
                    |Mp4UuidBoxDescriptor
                    |Mp4UuidBoxDirectory
                    |Mp4UuidBoxHandler
                    |Mp4VideoDescriptor
                    |Mp4VideoDirectory
                    |Mp4VideoHandler"""

    toConfigItems(classes = classes, pack = pack)
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

  private def comDrewMetadataPcx: List[String] = {
    val pack = "com.drew.metadata.pcx"
    val classes = """PcxDescriptor
                    |PcxDirectory
                    |PcxReader"""

    toConfigItems(classes = classes, pack = pack)
  }

  private def comDrewMetadataPlist: List[String] = {
    val pack = "com.drew.metadata.plist"
    val classes = """BplistReader"""

    toConfigItems(classes = classes, pack = pack)
  }

  private def comDrewMetadataPng: List[String] = {
    val pack = "com.drew.metadata.png"
    val classes = """PngChromaticitiesDirectory
                    |PngDescriptor
                    |PngDirectory""".stripMargin

    toConfigItems(classes = classes, pack = pack)
  }

  private def comDrewMetadataTiff: List[String] = {
    val pack = "com.drew.metadata.tiff"
    val classes = """DirectoryTiffHandler""".stripMargin

    toConfigItems(classes = classes, pack = pack)
  }

  private def comDrewMetadataWav: List[String] = {
    val pack = "com.drew.metadata.wav"
    val classes = """WavDescriptor
                    |WavDirectory
                    |WavRiffHandler""".stripMargin

    toConfigItems(classes = classes, pack = pack)
  }

  private def comDrewMetadataWebp: List[String] = {
    val pack = "com.drew.metadata.webp"
    val classes = """WebpDescriptor
                    |WebpDirectory
                    |WebpRiffHandler""".stripMargin

    toConfigItems(classes = classes, pack = pack)
  }

  private def comDrewMetadataXmp: List[String] = {
    val pack = "com.drew.metadata.xmp"
    val classes = """XmpDescriptor
                    |XmpDirectory
                    |XmpReader
                    |XmpWriter""".stripMargin

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
