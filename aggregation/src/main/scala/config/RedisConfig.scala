package ru.red.car_meta.aggregation.config

import cats.effect.Resource
import cats.effect.kernel.Async
import ciris.circe.yaml.circeYamlConfigDecoder
import ciris.{ConfigDecoder, file}
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

import java.nio.file.Paths

final case class RedisConfig(
  host: String,
  port: Int,
  carAdDlqName: String = "car_ad_dlq"
)
object RedisConfig {
  implicit val redisDecoder: Decoder[RedisConfig] = deriveDecoder[RedisConfig]

  implicit val redisConfigDecoder: ConfigDecoder[String, RedisConfig] =
    circeYamlConfigDecoder("RedisConfig")

  def load[F[_]: Async]: Resource[F, RedisConfig] =
    Resource.eval(file(Paths.get(getClass.getClassLoader.getResource("redis.yaml").getPath))
      .as[RedisConfig]
      .load[F])
}
