package ru.red.car_meta.aggregation
package config

import cats.effect.kernel.Async
import ciris.circe.yaml.circeYamlConfigDecoder
import ciris.{ConfigDecoder, file}
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

import java.nio.file.Paths

final case class KafkaConfig
(
  bootstrapServers: String,
  groupId: String
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