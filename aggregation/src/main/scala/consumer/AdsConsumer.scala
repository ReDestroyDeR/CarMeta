package ru.red.car_meta.aggregation
package consumer

import config.KafkaConfig

import cats.effect.Async
import cats.implicits._
import fs2.kafka.{ConsumerSettings, Deserializer}
import ru.red.car_meta.scraper.domain.car_domain.CarAd

object AdsConsumer {
  implicit def deserializer[F[_]: Async]: Deserializer[F, CarAd] =
    Deserializer.lift[F, CarAd](
      bytes => Async[F].delay(CarAd.parseFrom(bytes))
    )

  implicit def consumerSettings[F[_]: Async]: F[ConsumerSettings[F, String, CarAd]] = for {
    cfg <- KafkaConfig.load[F]
  } yield ConsumerSettings[F, String, CarAd]
    .withBootstrapServers(cfg.bootstrapServers)
}
