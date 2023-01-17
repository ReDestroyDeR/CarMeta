package ru.red.car_meta.aggregation
package config

import cats.effect.Resource
import cats.effect.kernel.Async
import ciris._
import ciris.circe.yaml._
import io.circe._
import io.circe.generic.semiauto._

import java.nio.file.Paths

final case class ElasticConfig
(
  host: String,
  carIndexName: String
)
object ElasticConfig {
  implicit val elasticDecoder: Decoder[ElasticConfig] = deriveDecoder[ElasticConfig]

  implicit val elasticConfigDecoder: ConfigDecoder[String, ElasticConfig] =
    circeYamlConfigDecoder("ElasticConfig")

  def load[F[_]: Async]: Resource[F, ElasticConfig] =
    Resource.eval(file(Paths.get(getClass.getClassLoader.getResource("elastic.yaml").getPath))
      .as[ElasticConfig]
      .load[F])
}
