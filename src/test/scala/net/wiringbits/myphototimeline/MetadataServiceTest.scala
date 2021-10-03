package net.wiringbits.myphototimeline

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.nio.file.Paths
import java.time.LocalDate

class MetadataServiceTest extends AnyWordSpec with Matchers {

  implicit val logger = new SimpleLogger(SimpleLogger.LogLevel.Info)

  "MetadataService" should {
    "discover metadata for a sample file" in {
      // given
      val metadataService = new MetadataService

      // when
      val result = metadataService.getCreationDate(os.Path(Paths.get(getClass.getResource("/NASA-scutum.jpg").toURI)))

      // test
      result shouldBe(Some(LocalDate.of(2012, 3, 8)))
    }
  }
}
