package ru.red.car_meta.scraper
package config

import cats.effect.kernel.Async
import ciris._
import ciris.circe.yaml._
import io.circe._
import io.circe.generic.semiauto._

import java.nio.file.Paths

final case class KafkaConfig(
  bootstrapServers: String
)
object KafkaConfig {
  implicit val kafkaDecoder: Decoder[KafkaConfig] = deriveDecoder[KafkaConfig]

  implicit val kafkaConfigDecoder: ConfigDecoder[String, KafkaConfig] =
    circeYamlConfigDecoder("KafkaConfig")

  def load[F[_]: Async]: F[KafkaConfig] =
    file(Paths.get(getClass.getClassLoader.getResource("kafka.yaml").getPath))
      .as[KafkaConfig]
      .load[F]
}
