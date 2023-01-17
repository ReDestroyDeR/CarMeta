package ru.red.car_meta.scraper
package config

import cats.effect.Resource
import cats.effect.kernel.Async
import ciris.circe.yaml.circeYamlConfigDecoder
import ciris.{ConfigDecoder, file}
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

import java.nio.file.Paths

final case class AvitoConfig(
  key: String,
  cookie: String,
  search: String,
  categoryId: Int,
  locationId: Int,
  searchRadius: Int,
  priceMin: Int,
  priceMax: Int,
  sort: String,
  withImagesOnly: Boolean,
  limit_page: Int,
  headers: Map[String, String]
)
object AvitoConfig {
  implicit val avitoDecoder: Decoder[AvitoConfig] = deriveDecoder[AvitoConfig]

  implicit val avitoConfigDecoder: ConfigDecoder[String, AvitoConfig] =
    circeYamlConfigDecoder("AvitoConfig")

  def load[F[_]: Async]: Resource[F, AvitoConfig] =
    Resource.eval(file(Paths.get(getClass.getClassLoader.getResource("avito.yaml").getPath))
      .as[AvitoConfig]
      .load[F])
}

